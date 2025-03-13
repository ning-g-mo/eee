package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 雷击事件 - 对随机玩家或实体施放雷击
 */
public class LightningEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    
    public LightningEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "lightning";
    }
    
    @Override
    public void execute() {
        // 获取所有在线玩家
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        
        if (players.isEmpty()) {
            plugin.getLogger().info("没有在线玩家，取消雷击事件");
            return;
        }
        
        // 决定是否只针对玩家，或者也包括其他实体
        boolean playersOnly = getBooleanSetting("players-only", false);
        
        List<LivingEntity> targets = new ArrayList<>();
        
        if (playersOnly) {
            targets.addAll(players);
        } else {
            // 随机选择一个玩家，然后获取其周围的实体
            Player randomPlayer = players.get(random.nextInt(players.size()));
            List<Entity> nearbyEntities = randomPlayer.getNearbyEntities(50, 50, 50);
            
            // 过滤出生物实体
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity) {
                    targets.add((LivingEntity) entity);
                }
            }
            
            // 如果没有找到附近的实体，就使用玩家作为目标
            if (targets.isEmpty()) {
                targets.addAll(players);
            }
        }
        
        // 随机选择目标数量
        int targetCount = Math.min(targets.size(), random.nextInt(5) + 1); // 1-5个目标
        
        for (int i = 0; i < targetCount; i++) {
            if (targets.isEmpty()) break;
            
            // 随机选择一个目标
            int index = random.nextInt(targets.size());
            LivingEntity target = targets.get(index);
            targets.remove(index); // 移除已选择的目标，避免重复
            
            // 获取目标位置
            Location location = target.getLocation();
            
            // 创建雷击
            target.getWorld().strikeLightning(location);
            
            // 如果目标是玩家，发送消息
            if (target instanceof Player) {
                ((Player) target).sendMessage("§c你被恶性事件的雷电击中了！");
            }
        }
    }
    
    @Override
    public boolean isServerDestructive() {
        return false; // 雷击事件不会破坏服务器
    }
}