package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 爆炸事件 - 在随机位置产生爆炸
 */
public class ExplosionEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    
    public ExplosionEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "explosion";
    }
    
    @Override
    public void execute() {
        // 获取所有在线玩家
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        
        if (players.isEmpty()) {
            plugin.getLogger().info("没有在线玩家，取消爆炸事件");
            return;
        }
        
        // 随机选择一个玩家
        Player targetPlayer = players.get(random.nextInt(players.size()));
        World world = targetPlayer.getWorld();
        
        // 获取爆炸强度
        float power = (float) getDoubleSetting("power", 4.0);
        
        // 在玩家附近随机位置创建爆炸
        int explosionCount = random.nextInt(5) + 1; // 1-5次爆炸
        
        for (int i = 0; i < explosionCount; i++) {
            // 在玩家周围20格范围内随机选择位置
            int offsetX = random.nextInt(41) - 20;
            int offsetY = random.nextInt(11) - 5;
            int offsetZ = random.nextInt(41) - 20;
            
            Location explosionLoc = targetPlayer.getLocation().clone().add(offsetX, offsetY, offsetZ);
            
            // 创建爆炸
            world.createExplosion(explosionLoc, power, true, true);
            
            // 延迟一点时间再创建下一个爆炸
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Override
    public boolean isServerDestructive() {
        return false; // 爆炸事件不会破坏服务器，只会破坏地形
    }
}