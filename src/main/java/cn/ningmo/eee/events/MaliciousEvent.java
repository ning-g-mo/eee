package cn.ningmo.eee.events;

import java.util.HashMap;
import java.util.Map;

/**
 * 恶性事件接口，所有恶性事件都应实现此接口
 */
public abstract class MaliciousEvent {
    
    private boolean enabled = true;
    private final Map<String, Object> settings = new HashMap<>();
    
    /**
     * 获取事件的唯一名称
     * @return 事件名称
     */
    public abstract String getName();
    
    /**
     * 执行事件
     */
    public abstract void execute();
    
    /**
     * 检查事件是否为服务器破坏性事件
     * @return 如果是破坏性事件返回true，否则返回false
     */
    public abstract boolean isServerDestructive();
    
    /**
     * 获取事件是否启用
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 设置事件是否启用
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * 设置事件特定设置
     * @param key 设置键
     * @param value 设置值
     */
    public void setSetting(String key, Object value) {
        settings.put(key, value);
    }
    
    /**
     * 获取事件特定设置
     * @param key 设置键
     * @return 设置值
     */
    public Object getSetting(String key) {
        return settings.get(key);
    }
    
    /**
     * 获取事件特定设置，如果不存在则返回默认值
     * @param key 设置键
     * @param defaultValue 默认值
     * @return 设置值或默认值
     */
    public Object getSetting(String key, Object defaultValue) {
        return settings.getOrDefault(key, defaultValue);
    }
    
    /**
     * 获取数值类型的设置
     * @param key 设置键
     * @param defaultValue 默认值
     * @return 数值设置
     */
    public double getDoubleSetting(String key, double defaultValue) {
        Object value = settings.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
    
    /**
     * 获取布尔类型的设置
     * @param key 设置键
     * @param defaultValue 默认值
     * @return 布尔设置
     */
    public boolean getBooleanSetting(String key, boolean defaultValue) {
        Object value = settings.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
} 