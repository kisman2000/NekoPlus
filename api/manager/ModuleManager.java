package me.vaimok.nekoplus.api.manager;

import me.vaimok.nekoplus.api.event.events.Render2DEvent;
import me.vaimok.nekoplus.api.event.events.Render3DEvent;
import me.vaimok.nekoplus.client.Client;
import me.vaimok.nekoplus.client.gui.nekoplusGui;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.modules.client.*;
import me.vaimok.nekoplus.client.modules.combat.*;
import me.vaimok.nekoplus.client.modules.misc.*;
import me.vaimok.nekoplus.client.modules.movement.*;
import me.vaimok.nekoplus.client.modules.player.*;
import me.vaimok.nekoplus.client.modules.player.InventoryManager;
import me.vaimok.nekoplus.client.modules.render.*;
import me.vaimok.nekoplus.api.util.moduleUtil.Util;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public
class ModuleManager extends Client {

    public static ArrayList < Module > modules = new ArrayList <> ( );
    public List < Module > sortedModules = new ArrayList <> ( );

    public static
    void onServerUpdate ( ) {
        modules.stream ( ).filter ( Client::isEnabled ).forEach ( Module::onServerUpdate );
    }

    public
    void init ( ) {
        //COMBAT
        modules.add ( new AutoTrap ( ) );
        modules.add ( new Offhand ( ) );
        modules.add ( new AutoArmor ( ) );
        modules.add ( new Surround ( ) );
        modules.add ( new HoleFill ( ) );
        modules.add ( new NekoAura ( ) );
        modules.add ( new WeebAura ( ) );
        modules.add ( new Quiver ( ) );

        //MISC
        modules.add ( new ChatSuffix ( ) );
        modules.add ( new Spammer ( ) );
        modules.add ( new NoSoundLag ( ) );
        modules.add ( new AutoEZ ( ) );
        modules.add ( new AutoSuicide ( ) );
        modules.add ( new QueueSkip ( ) );
        modules.add ( new WeaknessLog( ) );
        modules.add ( new BurrowCounter ( ) );

        //MOVEMENT
        modules.add ( new Velocity ( ) );
        modules.add ( new Step ( ) );
        modules.add ( new ReverseStep ( ) );
        modules.add ( new Sprint ( ) );
        modules.add ( new NoSlow ( ) );
        modules.add ( new NoWeb ( ) );
        modules.add ( new PerrySpeed( ) );
        modules.add ( new Strafe ( ) );

        //PLAYER
        modules.add ( new FakePlayer ( ) );
        modules.add ( new FastMine( ) );
        modules.add ( new InventoryManager( ) );
        modules.add ( new MCP ( ) );
        modules.add ( new PerryBurrow ( ) );
        modules.add ( new PacketMend ( ) );
        modules.add ( new Freecam ( ) );

        //RENDER
        modules.add ( new NoRender ( ) );
        modules.add ( new Fullbright ( ) );
        modules.add ( new Nametags ( ) );
        modules.add ( new CameraClip ( ) );
        modules.add ( new ViewModel ( ) );
        modules.add ( new HoleESP ( ) );

        //CLIENT
        modules.add ( new Colors ( ) );
        modules.add ( new Notifications ( ) );
        modules.add ( new FontMod ( ) );
        modules.add ( new HUD ( ) );
        modules.add ( new ClickGui ( ) );
        modules.add ( new Managers ( ) );
        modules.add ( new Components ( ) );
        modules.add ( new RPC ( ) );
        modules.add ( new GhastFinder());
    }

    public
    Module getModuleByName ( String name ) {
        for (Module module : modules) {
            if ( module.getName ( ).equalsIgnoreCase ( name ) ) {
                return module;
            }
        }
        return null;
    }

    public
    < T extends Module > T getModuleByClass ( Class < T > clazz ) {
        for (Module module : modules) {
            if ( clazz.isInstance ( module ) ) {
                return (T) module;
            }
        }
        return null;
    }

    public
    void enableModule ( Class clazz ) {
        Module module = getModuleByClass ( clazz );
        if ( module != null ) {
            module.enable ( );
        }
    }

    public
    void disableModule ( Class clazz ) {
        Module module = getModuleByClass ( clazz );
        if ( module != null ) {
            module.disable ( );
        }
    }

    public
    void enableModule ( String name ) {
        Module module = getModuleByName ( name );
        if ( module != null ) {
            module.enable ( );
        }
    }

    public
    void disableModule ( String name ) {
        Module module = getModuleByName ( name );
        if ( module != null ) {
            module.disable ( );
        }
    }

    public
    boolean isModuleEnabled ( String name ) {
        Module module = getModuleByName ( name );
        return module != null && module.isOn ( );
    }

    public
    boolean isModuleEnabled ( Class clazz ) {
        Module module = getModuleByClass ( clazz );
        return module != null && module.isOn ( );
    }

    public
    Module getModuleByDisplayName ( String displayName ) {
        for (Module module : modules) {
            if ( module.getDisplayName ( ).equalsIgnoreCase ( displayName ) ) {
                return module;
            }
        }
        return null;
    }

    public
    ArrayList < Module > getEnabledModules ( ) {
        ArrayList < Module > enabledModules = new ArrayList <> ( );
        for (Module module : modules) {
            if ( module.isEnabled ( ) ) {
                enabledModules.add ( module );
            }
        }
        return enabledModules;
    }

    public
    ArrayList < Module > getModulesByCategory ( Module.Category category ) {
        ArrayList < Module > modulesCategory = new ArrayList <> ( );
        modules.forEach ( module -> {
            if ( module.getCategory ( ) == category ) {
                modulesCategory.add ( module );
            }
        } );
        return modulesCategory;
    }

    public
    List < Module.Category > getCategories ( ) {
        return Arrays.asList ( Module.Category.values ( ) );
    }

    public
    void onLoad ( ) {
        modules.stream ( ).filter ( Module::listening ).forEach ( MinecraftForge.EVENT_BUS::register );
        modules.forEach ( Module::onLoad );
    }

    public
    void onUpdate ( ) {
        modules.stream ( ).filter ( Client::isEnabled ).forEach ( Module::onUpdate );
    }

    public
    void onTick ( ) {
        modules.stream ( ).filter ( Client::isEnabled ).forEach ( Module::onTick );
    }

    public
    void onRender2D ( Render2DEvent event ) {
        modules.stream ( ).filter ( Client::isEnabled ).forEach (module -> module.onRender2D ( event ) );
    }

    public
    void onRender3D ( Render3DEvent event ) {
        modules.stream ( ).filter ( Client::isEnabled ).forEach (module -> module.onRender3D ( event ) );
    }

    public
    void sortModules ( boolean reverse ) {
        this.sortedModules = getEnabledModules ( ).stream ( ).filter ( Module::isDrawn )
                .sorted ( Comparator.comparing ( module -> renderer.getStringWidth ( module.getFullArrayString ( ) ) * ( reverse ? - 1 : 1 ) ) )
                .collect ( Collectors.toList ( ) );
    }

    public
    void onLogout ( ) {
        modules.forEach ( Module::onLogout );
    }

    public
    void onLogin ( ) {
        modules.forEach ( Module::onLogin );
    }

    public
    void onUnload ( ) {
        modules.forEach ( MinecraftForge.EVENT_BUS::unregister );
        modules.forEach ( Module::onUnload );
    }

    public
    void onUnloadPost ( ) {
        for (Module module : modules) {
            module.enabled.setValue ( false );
        }
    }

    public
    void onKeyPressed ( int eventKey ) {
        if ( eventKey == 0 || ! Keyboard.getEventKeyState ( ) || Util.mc.currentScreen instanceof nekoplusGui ) {
            return;
        }
        modules.forEach ( module -> {
            if ( module.getBind ( ).getKey ( ) == eventKey ) {
                module.toggle ( );
            }
        } );
    }
}
