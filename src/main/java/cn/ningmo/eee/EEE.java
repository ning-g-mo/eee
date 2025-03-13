package cn.ningmo.eee;

import cn.ningmo.eee.api.EEEApi;
import cn.ningmo.eee.config.ConfigManager;
import cn.ningmo.eee.events.EventManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class EEE extends JavaPlugin {
    
    private static EEE instance;
    private ConfigManager configManager;
    private EventManager eventManager;
    private EEEApi api;
    private Logger logger;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        // 创建必要的文件夹
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // 初始化配置管理器
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // 初始化事件管理器
        eventManager = new EventManager(this);
        eventManager.loadEvents();
        
        // 初始化API
        api = new EEEApi(this);
        
        // 注册命令
        getCommand("eee").setExecutor(new EEECommand(this));
        
        logger.info("恶性事件插件已加载！作者：柠枺");
        logger.info("插件版本：" + getDescription().getVersion());
        logger.info("问题反馈&讨论群：603902151");
    }
    
    @Override
    public void onDisable() {
        // 保存配置
        configManager.saveConfig();
        
        logger.info("恶性事件插件已卸载！");
    }
    
    // 获取插件实例（单例模式）
    public static EEE getInstance() {
        return instance;
    }
    
    // 获取配置管理器
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    // 获取事件管理器
    public EventManager getEventManager() {
        return eventManager;
    }
    
    // 获取API
    public EEEApi getApi() {
        return api;
    }
}
