package cn.ningmo.eee.events;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.types.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EventManager {

    private final EEE plugin;
    private final Map<String, MaliciousEvent> registeredEvents;
    private final Random random;
    private BukkitTask eventTriggerTask;
    private boolean enabled;
    
    public EventManager(EEE plugin) {
        this.plugin = plugin;
        this.registeredEvents = new HashMap<>();
        this.random = ThreadLocalRandom.current();
        this.enabled = true;
    }
    
    public void loadEvents() {
        // 注册所有恶性事件
        registerEvents();
        
        // 启动事件触发任务
        startEventTrigger();
    }
    
    private void registerEvents() {
        // 注册默认事件
        registerEvent(new ExplosionEvent(plugin));
        registerEvent(new LightningEvent(plugin));
        registerEvent(new MobSpawnEvent(plugin));
        registerEvent(new ItemRemoveEvent(plugin));
        registerEvent(new PotionEvent(plugin));
        registerEvent(new WorldResetEvent(plugin));
        registerEvent(new PluginDisableEvent(plugin));
        registerEvent(new ServerCrashEvent(plugin));
        
        // 从配置文件中加载事件的启用状态
        Map<String, FileConfiguration> eventConfigs = plugin.getConfigManager().getEventConfigs();
        for (Map.Entry<String, FileConfiguration> entry : eventConfigs.entrySet()) {
            String eventName = entry.getKey();
            FileConfiguration config = entry.getValue();
            
            MaliciousEvent event = registeredEvents.get(eventName);
            if (event != null) {
                boolean enabled = config.getBoolean("enabled", true);
                event.setEnabled(enabled);
                
                // 加载事件特定的设置
                if (config.contains("settings")) {
                    for (String key : config.getConfigurationSection("settings").getKeys(false)) {
                        event.setSetting(key, config.get("settings." + key));
                    }
                }
            }
        }
    }
    
    private void registerEvent(MaliciousEvent event) {
        registeredEvents.put(event.getName(), event);
    }
    
    private void startEventTrigger() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        int interval = config.getInt("event-trigger-interval", 1200); // 默认1分钟
        
        enabled = config.getBoolean("events-enabled", true);
        
        if (enabled) {
            eventTriggerTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkAndTriggerEvent, interval, interval);
        }
    }
    
    public void stopEventTrigger() {
        if (eventTriggerTask != null) {
            eventTriggerTask.cancel();
            eventTriggerTask = null;
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        if (enabled && eventTriggerTask == null) {
            FileConfiguration config = plugin.getConfigManager().getConfig();
            int interval = config.getInt("event-trigger-interval", 1200);
            eventTriggerTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkAndTriggerEvent, interval, interval);
        } else if (!enabled && eventTriggerTask != null) {
            stopEventTrigger();
        }
        
        // 保存到配置
        plugin.getConfigManager().getConfig().set("events-enabled", enabled);
        plugin.getConfigManager().saveConfig();
    }
    
    private void checkAndTriggerEvent() {
        if (!enabled || Bukkit.getOnlinePlayers().isEmpty()) {
            return; // 如果没有玩家在线，不触发事件
        }
        
        // 检查是否应该触发任何事件
        List<MaliciousEvent> eligibleEvents = new ArrayList<>();
        
        for (MaliciousEvent event : registeredEvents.values()) {
            if (event.isEnabled() && shouldTriggerEvent(event)) {
                eligibleEvents.add(event);
            }
        }
        
        if (!eligibleEvents.isEmpty()) {
            // 随机选择一个事件触发
            MaliciousEvent eventToTrigger = eligibleEvents.get(random.nextInt(eligibleEvents.size()));
            triggerEvent(eventToTrigger);
        }
    }
    
    private boolean shouldTriggerEvent(MaliciousEvent event) {
        FileConfiguration eventConfig = plugin.getConfigManager().getEventConfig(event.getName());
        if (eventConfig == null) {
            return false;
        }
        
        double chance = eventConfig.getDouble("chance", 0.05);
        return random.nextDouble() < chance;
    }
    
    public void triggerEvent(String eventName) {
        MaliciousEvent event = registeredEvents.get(eventName);
        if (event != null && event.isEnabled()) {
            triggerEvent(event);
        }
    }
    
    private void triggerEvent(MaliciousEvent event) {
        // 创建事件触发事件
        MaliciousEventTriggerEvent triggerEvent = new MaliciousEventTriggerEvent(event);
        
        // 调用事件
        Bukkit.getPluginManager().callEvent(triggerEvent);
        
        // 如果事件被取消，直接返回
        if (triggerEvent.isCancelled()) {
            plugin.getLogger().info("恶性事件 " + event.getName() + " 的触发被其他插件取消");
            return;
        }
        
        // 检查是否是服务器破坏类事件，如果是则进行额外检查
        if (event.isServerDestructive()) {
            double destroyChance = plugin.getConfigManager().getConfig().getDouble("server-destroy-chance", 0.001);
            if (random.nextDouble() >= destroyChance) {
                // 未通过概率检查，不执行破坏性事件
                plugin.getLogger().info("尝试触发破坏性事件 " + event.getName() + " 但未通过概率检查");
                return;
            }
        }
        
        // 确定是否需要广播事件
        FileConfiguration eventConfig = plugin.getConfigManager().getEventConfig(event.getName());
        boolean announce = true;
        if (eventConfig != null) {
            announce = eventConfig.getBoolean("settings.announce", true);
        }
        
        if (announce) {
            String displayName = eventConfig != null ? 
                    eventConfig.getString("display-name", event.getName()) : 
                    event.getName();
            
            Bukkit.broadcastMessage("§c【恶性事件】§4" + displayName + " §c已被触发！");
        }
        
        // 执行事件
        try {
            event.execute();
            plugin.getLogger().info("成功触发恶性事件: " + event.getName());
        } catch (Exception e) {
            plugin.getLogger().severe("触发事件 " + event.getName() + " 时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Map<String, MaliciousEvent> getRegisteredEvents() {
        return registeredEvents;
    }
    
    public MaliciousEvent getEvent(String eventName) {
        return registeredEvents.get(eventName);
    }
    
    public boolean isEnabled() {
        return enabled;
    }
} 