package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 物品删除事件 - 随机删除玩家背包中的物品
 */
public class ItemRemoveEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    
    public ItemRemoveEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "item_remove";
    }
    
    @Override
    public void execute() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        
        if (players.isEmpty()) {
            plugin.getLogger().info("没有在线玩家，取消物品删除事件");
            return;
        }
        
        // 获取删除物品的数量
        int removeCount = (int) getDoubleSetting("remove-count", 3); // 默认删除3个物品
        boolean includeArmor = getBooleanSetting("include-armor", false); // 是否包括装备栏
        
        for (Player player : players) {
            PlayerInventory inventory = player.getInventory();
            List<Integer> nonEmptySlots = new ArrayList<>();
            
            // 收集所有非空的物品栏位置
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && !item.getType().isAir()) {
                    nonEmptySlots.add(i);
                }
            }
            
            // 如果包括装备栏，也加入列表
            if (includeArmor) {
                ItemStack[] armor = inventory.getArmorContents();
                for (int i = 0; i < armor.length; i++) {
                    if (armor[i] != null && !armor[i].getType().isAir()) {
                        nonEmptySlots.add(inventory.getSize() + i);
                    }
                }
            }
            
            // 如果没有物品可删除，跳过这个玩家
            if (nonEmptySlots.isEmpty()) {
                continue;
            }
            
            // 随机删除指定数量的物品
            int actualRemoveCount = Math.min(removeCount, nonEmptySlots.size());
            List<String> removedItems = new ArrayList<>();
            
            for (int i = 0; i < actualRemoveCount; i++) {
                if (nonEmptySlots.isEmpty()) break;
                
                int index = random.nextInt(nonEmptySlots.size());
                int slot = nonEmptySlots.get(index);
                nonEmptySlots.remove(index);
                
                ItemStack item;
                if (slot >= inventory.getSize()) {
                    // 装备栏物品
                    int armorSlot = slot - inventory.getSize();
                    item = inventory.getArmorContents()[armorSlot];
                    inventory.getArmorContents()[armorSlot] = null;
                } else {
                    // 普通物品栏物品
                    item = inventory.getItem(slot);
                    inventory.setItem(slot, null);
                }
                
                if (item != null) {
                    removedItems.add(item.getType().name().toLowerCase().replace("_", " "));
                }
            }
            
            // 更新玩家物品栏
            player.updateInventory();
            
            // 通知玩家
            if (!removedItems.isEmpty()) {
                player.sendMessage("§c你的以下物品已被恶性事件删除：§e" + String.join("§7, §e", removedItems));
            }
        }
    }
    
    @Override
    public boolean isServerDestructive() {
        return false; // 物品删除事件不会破坏服务器
    }
} 