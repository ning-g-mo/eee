package cn.ningmo.eee.api;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;

import java.util.Map;
import java.util.Set;

/**
 * 恶性事件插件API
 * 提供给其他插件使用的接口
 */
public class EEEApi {
    
    private final EEE plugin;
    
    public EEEApi(EEE plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 获取所有注册的恶性事件
     * @return 事件名称到事件对象的映射
     */
    public Map<String, MaliciousEvent> getRegisteredEvents() {
        return plugin.getEventManager().getRegisteredEvents();
    }
    
    /**
     * 获取所有注册的恶性事件名称
     * @return 事件名称集合
     */
    public Set<String> getRegisteredEventNames() {
        return plugin.getEventManager().getRegisteredEvents().keySet();
    }
    
    /**
     * 根据名称获取恶性事件
     * @param eventName 事件名称
     * @return 事件对象，如果不存在则返回null
     */
    public MaliciousEvent getEvent(String eventName) {
        return plugin.getEventManager().getEvent(eventName);
    }
    
    /**
     * 触发指定名称的恶性事件
     * @param eventName 事件名称
     * @return 是否成功触发（如果事件不存在或未启用则返回false）
     */
    public boolean triggerEvent(String eventName) {
        MaliciousEvent event = getEvent(eventName);
        if (event != null && event.isEnabled()) {
            plugin.getEventManager().triggerEvent(eventName);
            return true;
        }
        return false;
    }
    
    /**
     * 检查恶性事件系统是否启用
     * @return 是否启用
     */
    public boolean isEnabled() {
        return plugin.getEventManager().isEnabled();
    }
    
    /**
     * 设置恶性事件系统是否启用
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        plugin.getEventManager().setEnabled(enabled);
    }
    
    /**
     * 检查指定事件是否启用
     * @param eventName 事件名称
     * @return 是否启用，如果事件不存在则返回false
     */
    public boolean isEventEnabled(String eventName) {
        MaliciousEvent event = getEvent(eventName);
        return event != null && event.isEnabled();
    }
    
    /**
     * 设置指定事件是否启用
     * @param eventName 事件名称
     * @param enabled 是否启用
     * @return 是否成功设置（如果事件不存在则返回false）
     */
    public boolean setEventEnabled(String eventName, boolean enabled) {
        MaliciousEvent event = getEvent(eventName);
        if (event != null) {
            event.setEnabled(enabled);
            return true;
        }
        return false;
    }
} 