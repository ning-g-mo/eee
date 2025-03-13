package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 怪物生成事件 - 在玩家周围生成大量敌对生物
 */
public class MobSpawnEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    private final List<EntityType> hostileMobs = Arrays.asList(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.CREEPER,
        EntityType.SPIDER,
        EntityType.WITCH,
        EntityType.ENDERMAN,
        EntityType.BLAZE,
        EntityType.GHAST,
        EntityType.MAGMA_CUBE,
        EntityType.SLIME,
        EntityType.PHANTOM,
        EntityType.WITHER_SKELETON
    );
    
    public MobSpawnEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "mob_spawn";
    }
    
    @Override
    public void execute() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        
        if (players.isEmpty()) {
            plugin.getLogger().info("没有在线玩家，取消怪物生成事件");
            return;
        }
        
        // 获取生成数量
        int spawnCount = (int) getDoubleSetting("spawn-count", 30); // 默认生成30只怪物
        
        // 随机选择一个玩家作为目标
        Player targetPlayer = players.get(random.nextInt(players.size()));
        World world = targetPlayer.getWorld();
        Location playerLoc = targetPlayer.getLocation();
        
        // 在玩家周围生成怪物
        for (int i = 0; i < spawnCount; i++) {
            // 随机选择一种怪物
            EntityType mobType = hostileMobs.get(random.nextInt(hostileMobs.size()));
            
            // 在玩家周围20格范围内随机选择位置
            int offsetX = random.nextInt(41) - 20;
            int offsetZ = random.nextInt(41) - 20;
            
            Location spawnLoc = playerLoc.clone().add(offsetX, 0, offsetZ);
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);
            
            // 生成怪物
            world.spawnEntity(spawnLoc, mobType);
            
            // 添加一些延迟，避免服务器卡顿
            if (i % 5 == 0) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // 通知玩家
        targetPlayer.sendMessage("§c一大波怪物正在接近！");
    }
    
    @Override
    public boolean isServerDestructive() {
        return false; // 怪物生成事件不会破坏服务器
    }
} 