package me.vaimok.nekoplus.client.modules.movement;

import me.vaimok.nekoplus.api.event.events.BlockCollisionBoundingBoxEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.nekoplus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * @author Madmegsox
 * Ported and modified by Vaimok
 * @since 04/05/2021
 */

public
class NoWeb extends Module {


    public Setting < Boolean > disableBB = register ( new Setting ( "Add BB" , true ) );
    public Setting < Boolean > onGround = register ( new Setting ( "On Ground" , true ) );
    public Setting < Double > bbOffset = register ( new Setting ( "BB Offset" , 0.0 , - 2.0 , 2.0 ) );
    public Setting < Double > motionY = register ( new Setting ( "MotionY" , 1.0 , 0.0 , 20.0 ) );
    public Setting < Double > motionX = register ( new Setting ( "MotionX" , 0.84 , - 1.0 , 5.0 ) );  //FIX SETTINGS

    public
    NoWeb ( ) {
        super ( "NoWeb" , "antiretard" , Category.MOVEMENT , true , false , false );
    }


    @SubscribeEvent
    public
    void bbEvent ( BlockCollisionBoundingBoxEvent event ) {  //add block collision util
        if ( nullCheck ( ) ) return;
        if ( mc.world.getBlockState ( event.getPos ( ) ).getBlock ( ) instanceof BlockWeb ) {
            if ( disableBB.getValue ( ) ) {
                event.setCanceled ( true );
                event.setBoundingBox ( Block.FULL_BLOCK_AABB.contract ( 0 , bbOffset.getValue ( ) , 0 ) );
            }
        }
    }


    @Override
    public
    void onUpdate ( ) {
        if ( mc.player.isInWeb && ! nekoplus.moduleManager.isModuleEnabled ( "Step" ) ) {
            if ( Keyboard.isKeyDown ( mc.gameSettings.keyBindSneak.getKeyCode ( ) ) ) {
                mc.player.isInWeb = true;
                mc.player.motionY *= motionY.getValue ( );
            } else if ( onGround.getValue ( ) ) {
                mc.player.onGround = false;
            }
            if ( Keyboard.isKeyDown ( mc.gameSettings.keyBindForward.keyCode ) || Keyboard.isKeyDown ( mc.gameSettings.keyBindBack.keyCode ) || Keyboard.isKeyDown ( mc.gameSettings.keyBindLeft.keyCode )
                    || Keyboard.isKeyDown ( mc.gameSettings.keyBindRight.keyCode ) ) {
                mc.player.isInWeb = false;
                mc.player.motionX *= motionX.getValue ( );
                mc.player.motionZ *= motionX.getValue ( );
            }

        }
    }
}

