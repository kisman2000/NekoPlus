package me.vaimok.nekoplus.client.modules.movement;

import me.vaimok.nekoplus.api.event.events.KeyEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public
class NoSlow extends Module {

    private static final KeyBinding[] keys = {
            mc.gameSettings.keyBindForward ,
            mc.gameSettings.keyBindBack ,
            mc.gameSettings.keyBindLeft ,
            mc.gameSettings.keyBindRight ,
            mc.gameSettings.keyBindJump ,
            mc.gameSettings.keyBindSprint
    };
    private static NoSlow INSTANCE = new NoSlow ( );
    public Setting < Boolean > guiMove = register ( new Setting ( "GuiMove" , true ) );
    public Setting < Boolean > noSlow = register ( new Setting ( "NoSlow" , true ) );
    public Setting < Boolean > soulSand = register ( new Setting ( "SoulSand" , true ) );

    public
    NoSlow ( ) {
        super ( "NoSlow" , "Prevents you from getting slowed down." , Category.MOVEMENT , true , false , false );
        setInstance ( );
    }

    public static
    NoSlow getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new NoSlow ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onUpdate ( ) {
        if ( guiMove.getValue ( ) ) {
            if ( mc.currentScreen instanceof GuiOptions
                    || mc.currentScreen instanceof GuiVideoSettings
                    || mc.currentScreen instanceof GuiScreenOptionsSounds
                    || mc.currentScreen instanceof GuiContainer
                    || mc.currentScreen instanceof GuiIngameMenu ) {
                for (KeyBinding bind : keys) {
                    KeyBinding.setKeyBindState ( bind.getKeyCode ( ) , Keyboard.isKeyDown ( bind.getKeyCode ( ) ) );
                }
            } else if ( mc.currentScreen == null ) {
                for (KeyBinding bind : keys) {
                    if ( ! Keyboard.isKeyDown ( bind.getKeyCode ( ) ) ) {
                        KeyBinding.setKeyBindState ( bind.getKeyCode ( ) , false );
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public
    void onInput ( InputUpdateEvent event ) {
        if ( noSlow.getValue ( ) && mc.player.isHandActive ( ) && ! mc.player.isRiding ( ) ) {
            event.getMovementInput ( ).moveStrafe *= 5;
            event.getMovementInput ( ).moveForward *= 5;
        }
    }

    @SubscribeEvent
    public
    void onKeyEvent ( KeyEvent event ) {
        if ( guiMove.getValue ( ) && event.getStage ( ) == 0 && ! ( mc.currentScreen instanceof GuiChat ) ) {
            event.info = event.pressed;
        }
    }
}
