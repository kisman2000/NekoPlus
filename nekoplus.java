package me.vaimok.nekoplus;


import me.vaimok.nekoplus.api.manager.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = nekoplus.MODID, name = nekoplus.MODNAME, version = nekoplus.MODVER)
public
class nekoplus {

    public static final String MODID = "nekoplus";
    public static final String MODNAME = "nekoplus";
    public static final String MODVER = "0.4.6";
    public static final Logger LOGGER = LogManager.getLogger ( "nekoplus" );
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static NotificationManager notificationManager;
    public static SafetyManager safetyManager;
    @Mod.Instance
    public static nekoplus INSTANCE;
    private static String name = "nekoplus";
    private static boolean unloaded = false;

    public static
    String getName ( ) {
        return name;
    }

    public static
    void setName ( String newName ) {
        name = newName;
    }

    public static
    void load ( ) {
        LOGGER.info ( "\n\nLoading nekoplus " + MODVER );
        unloaded = false;
        if ( reloadManager != null ) {
            reloadManager.unload ( );
            reloadManager = null;
        }

        totemPopManager = new TotemPopManager ( );
        timerManager = new TimerManager ( );
        packetManager = new PacketManager ( );
        serverManager = new ServerManager ( );
        colorManager = new ColorManager ( );
        textManager = new TextManager ( );
        moduleManager = new ModuleManager ( );
        speedManager = new SpeedManager ( );
        rotationManager = new RotationManager ( );
        positionManager = new PositionManager ( );
        commandManager = new CommandManager ( );
        eventManager = new EventManager ( );
        configManager = new ConfigManager ( );
        fileManager = new FileManager ( );
        friendManager = new FriendManager ( );
        potionManager = new PotionManager ( );
        inventoryManager = new InventoryManager ( );
        holeManager = new HoleManager ( );
        notificationManager = new NotificationManager ( );
        safetyManager = new SafetyManager ( );
        LOGGER.info ( "Initialized Managers" );

        moduleManager.init ( );
        LOGGER.info ( "Modules loaded." );
        configManager.init ( );
        eventManager.init ( );
        LOGGER.info ( "EventManager loaded." );
        textManager.init ( true );
        moduleManager.onLoad ( );
        totemPopManager.init ( );
        timerManager.init ( );
        LOGGER.info ( "nekoplus initialized!\n" );

        Runtime.getRuntime().addShutdownHook(new Thread(DiscordUtil::shutdown));
    }

    public static
    void unload ( boolean unload ) {
        LOGGER.info ( "\n\nUnloading nekoplus " + MODVER );
        if ( unload ) {
            reloadManager = new ReloadManager ( );
            reloadManager.init ( commandManager != null ? commandManager.getPrefix ( ) : "." );
        }
        onUnload ( );
        eventManager = null;
        holeManager = null;
        timerManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        positionManager = null;
        commandManager = null;
        configManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        notificationManager = null;
        safetyManager = null;
        LOGGER.info ( "nekoplus unloaded!\n" );
    }

    public static
    void reload ( ) {
        unload ( false );
        load ( );
    }

    public static
    void onUnload ( ) {
        if ( ! unloaded ) {
            eventManager.onUnload ( );
            moduleManager.onUnload ( );
            configManager.saveConfig ( configManager.config.replaceFirst ( "nekoplus/" , "" ) );
            moduleManager.onUnloadPost ( );
            timerManager.unload ( );
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public
    void preInit ( FMLPreInitializationEvent event ) {
    }



    @Mod.EventHandler
    public
    void init ( FMLInitializationEvent event ) {
        Display.setTitle ( "nekoplus - v." + MODVER );
        load ( );
    }

}

