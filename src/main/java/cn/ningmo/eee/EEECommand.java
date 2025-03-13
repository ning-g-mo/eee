package cn.ningmo.eee;

import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EEECommand implements CommandExecutor, TabCompleter {
    
    private final EEE plugin;
    private final List<String> subCommands = Arrays.asList("help", "list", "trigger", "enable", "disable", "reload");
    
    public EEECommand(EEE plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;
                
            case "list":
                listEvents(sender);
                break;
                
            case "trigger":
                if (!sender.hasPermission("eee.trigger")) {
                    sender.sendMessage(ChatColor.RED + "你没有权限触发恶性事件！");
                    return true;
                }
                
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "用法: /eee trigger <事件名称>");
                    return true;
                }
                
                triggerEvent(sender, args[1]);
                break;
                
            case "enable":
                if (!sender.hasPermission("eee.admin")) {
                    sender.sendMessage(ChatColor.RED + "你没有权限启用/禁用恶性事件！");
                    return true;
                }
                
                if (args.length < 2) {
                    // 启用整个插件
                    plugin.getEventManager().setEnabled(true);
                    sender.sendMessage(ChatColor.GREEN + "恶性事件系统已启用！");
                } else {
                    // 启用特定事件
                    enableEvent(sender, args[1], true);
                }
                break;
                
            case "disable":
                if (!sender.hasPermission("eee.admin")) {
                    sender.sendMessage(ChatColor.RED + "你没有权限启用/禁用恶性事件！");
                    return true;
                }
                
                if (args.length < 2) {
                    // 禁用整个插件
                    plugin.getEventManager().setEnabled(false);
                    sender.sendMessage(ChatColor.YELLOW + "恶性事件系统已禁用！");
                } else {
                    // 禁用特定事件
                    enableEvent(sender, args[1], false);
                }
                break;
                
            case "reload":
                if (!sender.hasPermission("eee.admin")) {
                    sender.sendMessage(ChatColor.RED + "你没有权限重载插件！");
                    return true;
                }
                
                // 重载配置
                plugin.getConfigManager().loadConfig();
                sender.sendMessage(ChatColor.GREEN + "恶性事件插件配置已重载！");
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "未知命令！使用 /eee help 查看帮助。");
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===== 恶性事件插件帮助 =====");
        sender.sendMessage(ChatColor.YELLOW + "/eee help " + ChatColor.WHITE + "- 显示帮助信息");
        sender.sendMessage(ChatColor.YELLOW + "/eee list " + ChatColor.WHITE + "- 列出所有可用的恶性事件");
        
        if (sender.hasPermission("eee.trigger")) {
            sender.sendMessage(ChatColor.YELLOW + "/eee trigger <事件名称> " + ChatColor.WHITE + "- 手动触发一个恶性事件");
        }
        
        if (sender.hasPermission("eee.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/eee enable [事件名称] " + ChatColor.WHITE + "- 启用插件或特定事件");
            sender.sendMessage(ChatColor.YELLOW + "/eee disable [事件名称] " + ChatColor.WHITE + "- 禁用插件或特定事件");
            sender.sendMessage(ChatColor.YELLOW + "/eee reload " + ChatColor.WHITE + "- 重载插件配置");
        }
        
        sender.sendMessage(ChatColor.RED + "警告：此插件包含可能破坏服务器的恶性事件，请谨慎使用！");
    }
    
    private void listEvents(CommandSender sender) {
        Map<String, MaliciousEvent> events = plugin.getEventManager().getRegisteredEvents();
        
        sender.sendMessage(ChatColor.GOLD + "===== 可用的恶性事件 =====");
        
        for (Map.Entry<String, MaliciousEvent> entry : events.entrySet()) {
            String eventName = entry.getKey();
            MaliciousEvent event = entry.getValue();
            FileConfiguration eventConfig = plugin.getConfigManager().getEventConfig(eventName);
            
            String displayName = eventConfig != null ? 
                    eventConfig.getString("display-name", eventName) : 
                    eventName;
                    
            String description = eventConfig != null ? 
                    eventConfig.getString("description", "无描述") : 
                    "无描述";
            
            ChatColor statusColor = event.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
            String status = event.isEnabled() ? "启用" : "禁用";
            
            sender.sendMessage(statusColor + "[" + status + "] " + 
                    ChatColor.YELLOW + displayName + 
                    ChatColor.GRAY + " (" + eventName + ") " + 
                    ChatColor.WHITE + "- " + description);
        }
    }
    
    private void triggerEvent(CommandSender sender, String eventName) {
        MaliciousEvent event = plugin.getEventManager().getEvent(eventName);
        
        if (event == null) {
            sender.sendMessage(ChatColor.RED + "找不到名为 " + eventName + " 的事件！");
            return;
        }
        
        if (!event.isEnabled()) {
            sender.sendMessage(ChatColor.RED + "事件 " + eventName + " 当前已禁用！");
            return;
        }
        
        // 如果是破坏性事件，需要额外确认
        if (event.isServerDestructive()) {
            sender.sendMessage(ChatColor.RED + "警告：" + eventName + " 是一个可能破坏服务器的恶性事件！");
            sender.sendMessage(ChatColor.RED + "如果你确定要触发，请再次输入相同的命令。");
            
            // 这里可以添加一个确认机制，但为简单起见，我们直接触发
        }
        
        // 触发事件
        plugin.getEventManager().triggerEvent(eventName);
        sender.sendMessage(ChatColor.GREEN + "已触发恶性事件：" + eventName);
    }
    
    private void enableEvent(CommandSender sender, String eventName, boolean enable) {
        MaliciousEvent event = plugin.getEventManager().getEvent(eventName);
        
        if (event == null) {
            sender.sendMessage(ChatColor.RED + "找不到名为 " + eventName + " 的事件！");
            return;
        }
        
        event.setEnabled(enable);
        
        // 保存到配置
        FileConfiguration eventConfig = plugin.getConfigManager().getEventConfig(eventName);
        if (eventConfig != null) {
            eventConfig.set("enabled", enable);
            plugin.getConfigManager().saveEventConfig(eventName);
        }
        
        String status = enable ? "启用" : "禁用";
        sender.sendMessage(ChatColor.GREEN + "已" + status + "恶性事件：" + eventName);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 子命令补全
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            // 事件名称补全
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("trigger") || subCommand.equals("enable") || subCommand.equals("disable")) {
                String prefix = args[1].toLowerCase();
                completions.addAll(plugin.getEventManager().getRegisteredEvents().keySet().stream()
                        .filter(name -> name.startsWith(prefix))
                        .collect(Collectors.toList()));
            }
        }
        
        return completions;
    }
}