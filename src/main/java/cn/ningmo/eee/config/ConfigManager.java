package cn.ningmo.eee.config;

import cn.ningmo.eee.EEE;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {
    
    private final EEE plugin;
    private FileConfiguration config;
    private final File configFile;
    private final Map<String, FileConfiguration> eventConfigs;
    private final File eventFolder;
    
    public ConfigManager(EEE plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.eventFolder = new File(plugin.getDataFolder(), "e");
        this.eventConfigs = new HashMap<>();
        
        // 确保事件文件夹存在
        if (!eventFolder.exists()) {
            eventFolder.mkdirs();
        }
    }
    
    public void loadConfig() {
        // 加载主配置文件
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // 设置默认值（如果配置中没有）
        if (!config.contains("server-destroy-chance")) {
            config.set("server-destroy-chance", 0.001); // 默认0.1%的概率
        }
        
        if (!config.contains("events-enabled")) {
            config.set("events-enabled", true); // 默认启用事件
        }
        
        if (!config.contains("event-trigger-interval")) {
            config.set("event-trigger-interval", 1200); // 默认每1200 tick（1分钟）检查一次
        }
        
        saveConfig();
        
        // 加载事件配置文件
        loadEventConfigs();
    }
    
    private void loadEventConfigs() {
        File[] eventFiles = eventFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (eventFiles == null || eventFiles.length == 0) {
            // 创建默认事件配置
            createDefaultEventConfigs();
            eventFiles = eventFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        }
        
        if (eventFiles != null) {
            for (File file : eventFiles) {
                String eventName = file.getName().replace(".yml", "");
                FileConfiguration eventConfig = YamlConfiguration.loadConfiguration(file);
                eventConfigs.put(eventName, eventConfig);
            }
        }
    }
    
    private void createDefaultEventConfigs() {
        // 创建几个默认的恶性事件配置
        createEventConfig("explosion", "爆炸事件", 0.05, "在随机位置产生爆炸");
        createEventConfig("lightning", "雷击事件", 0.1, "对随机玩家或实体施放雷击");
        createEventConfig("mob_spawn", "怪物生成", 0.15, "生成一大波敌对生物");
        createEventConfig("item_remove", "物品消失", 0.08, "随机删除玩家背包中的物品");
        createEventConfig("potion", "负面药水", 0.2, "给玩家施加负面药水效果");
        createEventConfig("world_reset", "世界重置", 0.001, "尝试删除或重置世界数据");
        createEventConfig("plugin_disable", "插件禁用", 0.01, "随机禁用服务器上的一个插件");
        createEventConfig("server_crash", "服务器崩溃", 0.005, "尝试让服务器崩溃");
    }
    
    private void createEventConfig(String name, String displayName, double chance, String description) {
        File eventFile = new File(eventFolder, name + ".yml");
        if (!eventFile.exists()) {
            FileConfiguration eventConfig = new YamlConfiguration();
            eventConfig.set("enabled", true);
            eventConfig.set("chance", chance);
            eventConfig.set("display-name", displayName);
            eventConfig.set("description", description);
            // 每个事件的特定配置
            eventConfig.set("settings.power", 1.0); // 事件强度
            eventConfig.set("settings.announce", true); // 是否公告事件
            
            try {
                eventConfig.save(eventFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "无法创建事件配置文件: " + name, e);
            }
        }
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存配置文件", e);
        }
    }
    
    public void saveEventConfig(String eventName) {
        FileConfiguration eventConfig = eventConfigs.get(eventName);
        if (eventConfig != null) {
            try {
                eventConfig.save(new File(eventFolder, eventName + ".yml"));
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "无法保存事件配置文件: " + eventName, e);
            }
        }
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public Map<String, FileConfiguration> getEventConfigs() {
        return eventConfigs;
    }
    
    public FileConfiguration getEventConfig(String eventName) {
        return eventConfigs.get(eventName);
    }
} 