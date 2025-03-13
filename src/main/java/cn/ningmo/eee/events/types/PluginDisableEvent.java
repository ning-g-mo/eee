package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 插件禁用事件 - 随机禁用服务器上的插件（破坏性事件）
 */
public class PluginDisableEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    
    public PluginDisableEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "plugin_disable";
    }
    
    @Override
    public void execute() {
        // 获取所有插件
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        List<Plugin> eligiblePlugins = new ArrayList<>();
        
        // 过滤掉不能禁用的插件
        for (Plugin p : plugins) {
            if (!p.equals(plugin) && // 不能禁用自己
                !p.getName().equalsIgnoreCase("Essentials") && // 不禁用基础插件
                !p.getName().equalsIgnoreCase("PermissionsEx") && // 不禁用权限插件
                !p.getName().equalsIgnoreCase("LuckPerms")) { // 不禁用权限插件
                eligiblePlugins.add(p);
            }
        }
        
        if (eligiblePlugins.isEmpty()) {
            plugin.getLogger().warning("没有找到可以禁用的插件");
            return;
        }
        
        // 获取要禁用的插件数量
        int disableCount = (int) getDoubleSetting("disable-count", 1); // 默认禁用1个插件
        boolean deleteFiles = getBooleanSetting("delete-files", false); // 是否删除插件文件
        
        // 随机禁用插件
        for (int i = 0; i < disableCount && !eligiblePlugins.isEmpty(); i++) {
            int index = random.nextInt(eligiblePlugins.size());
            Plugin targetPlugin = eligiblePlugins.get(index);
            eligiblePlugins.remove(index);
            
            String pluginName = targetPlugin.getName();
            
            try {
                // 禁用插件
                Bukkit.getPluginManager().disablePlugin(targetPlugin);
                
                // 如果配置了删除文件，则删除插件文件
                if (deleteFiles) {
                    try {
                        File pluginFile = new File(targetPlugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                        if (pluginFile.exists() && pluginFile.delete()) {
                            Bukkit.broadcastMessage("§c【恶性事件】插件 " + pluginName + " 已被删除！");
                        }
                    } catch (URISyntaxException e) {
                        plugin.getLogger().warning("无法获取插件 " + pluginName + " 的文件位置");
                    }
                } else {
                    Bukkit.broadcastMessage("§c【恶性事件】插件 " + pluginName + " 已被禁用！");
                }
                
                plugin.getLogger().warning("插件 " + pluginName + " 已被恶性事件" + (deleteFiles ? "删除" : "禁用"));
                
            } catch (Exception e) {
                plugin.getLogger().warning("尝试" + (deleteFiles ? "删除" : "禁用") + "插件 " + pluginName + " 时发生错误");
            }
        }
    }
    
    @Override
    public boolean isServerDestructive() {
        return true; // 这是一个破坏性事件
    }
} 