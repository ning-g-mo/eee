package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 世界重置事件 - 尝试删除或重置世界数据（破坏性事件）
 */
public class WorldResetEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    
    public WorldResetEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "world_reset";
    }
    
    @Override
    public void execute() {
        // 获取所有世界
        World targetWorld = null;
        String worldName = (String) getSetting("target-world", "");
        
        if (!worldName.isEmpty()) {
            targetWorld = Bukkit.getWorld(worldName);
        } else {
            // 随机选择一个世界
            if (!Bukkit.getWorlds().isEmpty()) {
                targetWorld = Bukkit.getWorlds().get(random.nextInt(Bukkit.getWorlds().size()));
            }
        }
        
        if (targetWorld == null) {
            plugin.getLogger().warning("没有找到可以重置的世界");
            return;
        }
        
        // 获取世界文件夹
        File worldFolder = targetWorld.getWorldFolder();
        
        // 踢出世界中的所有玩家
        for (Player player : targetWorld.getPlayers()) {
            World defaultWorld = Bukkit.getWorlds().get(0);
            if (defaultWorld != null && !defaultWorld.equals(targetWorld)) {
                player.teleport(defaultWorld.getSpawnLocation());
                player.sendMessage("§c警告：你所在的世界即将被重置！");
            }
        }
        
        // 卸载世界
        if (Bukkit.unloadWorld(targetWorld, false)) {
            // 删除世界文件
            boolean deleteSuccess = deleteWorld(worldFolder);
            
            if (deleteSuccess) {
                Bukkit.broadcastMessage("§c【恶性事件】世界 " + targetWorld.getName() + " 已被删除！");
                plugin.getLogger().warning("世界 " + targetWorld.getName() + " 已被恶性事件删除");
            } else {
                plugin.getLogger().warning("尝试删除世界 " + targetWorld.getName() + " 失败");
            }
        } else {
            plugin.getLogger().warning("无法卸载世界 " + targetWorld.getName());
        }
    }
    
    private boolean deleteWorld(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorld(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return path.delete();
    }
    
    @Override
    public boolean isServerDestructive() {
        return true; // 这是一个破坏性事件
    }
} 