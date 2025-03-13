# 恶性事件插件 (EEE)

一个为服务器添加随机恶性事件的插件，这些事件可能会给玩家带来不同程度的影响，严重的甚至可能导致服务器崩溃。

## 作者信息
- 作者：柠枺
- 问题反馈&讨论群：603902151

## 插件特点
- 多种恶性事件类型
- 可配置的触发概率
- 完整的API支持
- 权限系统支持
- 详细的配置选项

## 命令用法

### 基础命令
- `/eee help` - 显示帮助信息
- `/eee list` - 列出所有可用的恶性事件
- `/eee reload` - 重新加载配置文件

### 事件控制
- `/eee trigger <事件名称>` - 手动触发指定事件
- `/eee enable [事件名称]` - 启用插件或指定事件
- `/eee disable [事件名称]` - 禁用插件或指定事件

### 权限节点
- `eee.admin` - 管理员权限，允许使用所有命令
- `eee.trigger` - 允许手动触发事件
- `eee.reload` - 允许重载配置
- `eee.enable` - 允许启用事件
- `eee.disable` - 允许禁用事件

## API 使用方法

### 获取API实例
```java
// 获取插件实例
EEE plugin = (EEE) Bukkit.getPluginManager().getPlugin("EEE");
// 获取API实例
EEEApi api = plugin.getApi();
```

### 事件管理
```java
// 获取所有注册的恶性事件
Map<String, MaliciousEvent> events = api.getRegisteredEvents();

// 获取所有事件名称
Set<String> eventNames = api.getRegisteredEventNames();

// 获取特定事件
MaliciousEvent event = api.getEvent("explosion");

// 检查事件是否启用
boolean isEnabled = api.isEventEnabled("explosion");

// 启用/禁用事件
api.setEventEnabled("explosion", true);
```

### 触发事件
```java
// 触发特定事件
boolean success = api.triggerEvent("explosion");

// 检查系统是否启用
boolean isSystemEnabled = api.isEnabled();

// 启用/禁用整个系统
api.setEnabled(true);
```

### 事件监听示例
```java
public class ExampleListener implements Listener {
    @EventHandler
    public void onMaliciousEvent(MaliciousEventTriggerEvent event) {
        // 获取触发的事件
        MaliciousEvent maliciousEvent = event.getEvent();
        
        // 获取事件名称
        String eventName = maliciousEvent.getName();
        
        // 检查是否是破坏性事件
        boolean isDestructive = maliciousEvent.isServerDestructive();
        
        // 可以取消事件
        if (isDestructive) {
            event.setCancelled(true);
        }
    }
}
```

## 配置文件说明

### 主配置文件 (config.yml)
```yaml
# 是否启用恶性事件系统
events-enabled: true

# 事件触发间隔（以tick为单位，20 tick = 1秒）
event-trigger-interval: 1200  # 默认1分钟

# 破坏性事件的触发概率（0-1之间）
server-destroy-chance: 0.001  # 默认0.1%的概率
```

### 事件配置文件 (e/*.yml)
每个事件都有自己的配置文件，位于插件目录的 `e` 文件夹中。

示例（爆炸事件）：
```yaml
enabled: true
chance: 0.05  # 触发概率
display-name: "爆炸事件"
description: "在随机位置产生爆炸"

settings:
  power: 4.0  # 爆炸威力
  announce: true  # 是否公告
```

## 注意事项

1. 破坏性事件
   - 世界重置事件
   - 插件禁用事件
   - 服务器崩溃事件
   
   这些事件可能会导致服务器不稳定或无法启动，请谨慎使用。

2. 配置建议
   - 建议将破坏性事件的概率设置得很低
   - 保护重要的世界和插件
   - 定期备份服务器数据

3. 安全建议
   - 仅给信任的管理员触发事件的权限
   - 在测试服务器上先测试配置
   - 保持主要插件在保护列表中

## 常见问题

1. 如何禁用特定事件？
   - 在对应事件的配置文件中设置 `enabled: false`
   - 使用命令 `/eee disable <事件名称>`

2. 如何调整事件触发频率？
   - 在 `config.yml` 中修改 `event-trigger-interval`

3. 如何防止重要插件被禁用？
   - 在 `plugin_disable.yml` 的 `protected-plugins` 列表中添加插件名称

4. 如何保护重要世界？
   - 在 `world_reset.yml` 的 `protected-worlds` 列表中添加世界名称 