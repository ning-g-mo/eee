package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负面药水效果事件 - 给玩家施加随机负面药水效果
 */
public class PotionEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    private final List<PotionEffectType> negativeEffects = Arrays.asList(
        PotionEffectType.BLINDNESS,
        PotionEffectType.DARKNESS,
        PotionEffectType.INSTANT_DAMAGE,
        PotionEffectType.HUNGER,
        PotionEffectType.POISON,
        PotionEffectType.SLOWNESS,
        PotionEffectType.WEAKNESS,
        PotionEffectType.WITHER,
        PotionEffectType.UNLUCK,
        PotionEffectType.LEVITATION
    );
    
    public PotionEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "potion";
    }
    
    @Override
    public void execute() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        
        if (players.isEmpty()) {
            plugin.getLogger().info("没有在线玩家，取消药水效果事件");
            return;
        }
        
        // 获取效果数量和持续时间
        int effectCount = (int) getDoubleSetting("effect-count", 3); // 默认3个效果
        int duration = (int) getDoubleSetting("duration", 600); // 默认30秒（600 ticks）
        int maxAmplifier = (int) getDoubleSetting("max-amplifier", 2); // 默认最高等级2
        
        for (Player player : players) {
            // 随机选择几个效果
            List<PotionEffectType> selectedEffects = new ArrayList<>(negativeEffects);
            List<String> appliedEffects = new ArrayList<>();
            
            for (int i = 0; i < effectCount; i++) {
                if (selectedEffects.isEmpty()) break;
                
                // 随机选择一个效果
                int index = random.nextInt(selectedEffects.size());
                PotionEffectType effectType = selectedEffects.get(index);
                selectedEffects.remove(index);
                
                // 随机效果等级
                int amplifier = random.nextInt(maxAmplifier + 1);
                
                // 创建和应用效果
                PotionEffect effect = new PotionEffect(
                    effectType,
                    duration + random.nextInt(200), // 添加一些随机持续时间
                    amplifier,
                    true, // 显示粒子
                    true, // 显示图标
                    true  // 环境效果
                );
                
                player.addPotionEffect(effect);
                
                // 记录效果名称
                String effectName = effectType.getName().toLowerCase().replace("_", " ");
                appliedEffects.add(effectName + " " + (amplifier + 1));
            }
            
            // 通知玩家
            if (!appliedEffects.isEmpty()) {
                player.sendMessage("§c你被施加了以下负面效果：§e" + String.join("§7, §e", appliedEffects));
            }
        }
    }
    
    @Override
    public boolean isServerDestructive() {
        return false; // 药水效果事件不会破坏服务器
    }
} 