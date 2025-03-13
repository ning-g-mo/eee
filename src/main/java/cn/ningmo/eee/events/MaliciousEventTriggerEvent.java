package cn.ningmo.eee.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 恶性事件触发事件
 * 当一个恶性事件即将被触发时调用
 * 其他插件可以监听此事件来干预恶性事件的触发
 */
public class MaliciousEventTriggerEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final MaliciousEvent event;
    
    public MaliciousEventTriggerEvent(MaliciousEvent event) {
        this.event = event;
    }
    
    /**
     * 获取即将被触发的恶性事件
     * @return 恶性事件实例
     */
    public MaliciousEvent getEvent() {
        return event;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
} 