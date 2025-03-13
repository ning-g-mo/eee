package cn.ningmo.eee.events.types;

import cn.ningmo.eee.EEE;
import cn.ningmo.eee.events.MaliciousEvent;
import org.bukkit.Bukkit;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 服务器崩溃事件 - 尝试让服务器崩溃（破坏性事件）
 */
public class ServerCrashEvent extends MaliciousEvent {
    
    private final EEE plugin;
    private final Random random;
    
    public ServerCrashEvent(EEE plugin) {
        this.plugin = plugin;
        this.random = ThreadLocalRandom.current();
    }
    
    @Override
    public String getName() {
        return "server_crash";
    }
    
    @Override
    public void execute() {
        // 获取崩溃方式
        int crashMethod = random.nextInt(4);
        
        Bukkit.broadcastMessage("§c【恶性事件】服务器即将崩溃！");
        plugin.getLogger().severe("服务器崩溃事件已被触发！");
        
        // 延迟一下，让消息能发送出去
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        switch (crashMethod) {
            case 0:
                // 方法1：触发OutOfMemoryError
                try {
                    Object[] data = new Object[Integer.MAX_VALUE];
                    while (true) {
                        data[0] = new byte[Integer.MAX_VALUE - 2];
                    }
                } catch (Throwable ignored) {
                }
                break;
                
            case 1:
                // 方法2：无限递归导致StackOverflowError
                crashRecursively(1);
                break;
                
            case 2:
                // 方法3：关闭JVM
                System.exit(1);
                break;
                
            case 3:
                // 方法4：抛出严重错误
                throw new Error("恶性事件触发的服务器崩溃");
                
            default:
                // 备用方法：直接关闭服务器
                Bukkit.shutdown();
                break;
        }
    }
    
    private void crashRecursively(int depth) {
        // 无限递归直到栈溢出
        crashRecursively(depth + 1);
    }
    
    @Override
    public boolean isServerDestructive() {
        return true; // 这是一个破坏性事件
    }
} 