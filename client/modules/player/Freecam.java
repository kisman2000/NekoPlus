package me.vaimok.nekoplus.client.modules.player;

import me.vaimok.nekoplus.api.event.events.PacketEvent;
import me.vaimok.nekoplus.api.event.events.PushEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//TODO: legit mode that doesnt cancel packets but changes them to legit packets
public
class Freecam extends Module {

    private static Freecam INSTANCE = new Freecam ( );
    public Setting < Double > speed = register ( new Setting ( "Speed" , 0.5 , 0.1 , 5.0 ) ); //Weird rounding issues when this was a float...
    public Setting < Boolean > view = register ( new Setting ( "3D" , false ) );
    public Setting < Boolean > packet = register ( new Setting ( "Packet" , true ) );
    public Setting < Boolean > disable = register ( new Setting ( "Logout/Off" , true ) );
    private AxisAlignedBB oldBoundingBox;
    private EntityOtherPlayerMP entity;
    private Vec3d position;
    private Entity riding;
    private float yaw;
    private float pitch;

    public
    Freecam ( ) {
        super ( "Freecam" , "Look around freely." , Category.PLAYER , true , false , false );
        setInstance ( );
    }

    public static
    Freecam getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new Freecam ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onEnable ( ) {
        if ( ! fullNullCheck ( ) ) {
            oldBoundingBox = mc.player.getEntityBoundingBox ( );
            mc.player.setEntityBoundingBox ( new AxisAlignedBB ( mc.player.posX , mc.player.posY , mc.player.posZ , mc.player.posX , mc.player.posY , mc.player.posZ ) );
            if ( mc.player.getRidingEntity ( ) != null ) {
                riding = mc.player.getRidingEntity ( );
                mc.player.dismountRidingEntity ( );
            }
            entity = new EntityOtherPlayerMP ( mc.world , mc.session.getProfile ( ) );
            entity.copyLocationAndAnglesFrom ( mc.player );
            entity.rotationYaw = mc.player.rotationYaw;
            entity.rotationYawHead = mc.player.rotationYawHead;
            entity.inventory.copyInventory ( mc.player.inventory );
            mc.world.addEntityToWorld ( 69420 , entity );
            position = mc.player.getPositionVector ( );
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            mc.player.noClip = true;
        }
    }

    @Override
    public
    void onDisable ( ) {
        if ( ! fullNullCheck ( ) ) {
            mc.player.setEntityBoundingBox ( oldBoundingBox );
            if ( riding != null ) {
                mc.player.startRiding ( riding , true );
            }
            if ( entity != null ) {
                mc.world.removeEntity ( entity );
            }
            if ( position != null ) {
                mc.player.setPosition ( position.x , position.y , position.z );
            }
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
            mc.player.noClip = false;
        }
    }

    @Override
    public
    void onUpdate ( ) {
        mc.player.noClip = true;
        mc.player.setVelocity ( 0 , 0 , 0 );
        mc.player.jumpMovementFactor = speed.getValue ( ).floatValue ( );
        double[] dir = MathUtil.directionSpeed ( speed.getValue ( ) );
        if ( mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0 ) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        mc.player.setSprinting ( false );
        if ( view.getValue ( ) && ! ( mc.gameSettings.keyBindSneak.isKeyDown ( ) || mc.gameSettings.keyBindJump.isKeyDown ( ) ) ) {
            mc.player.motionY = ( speed.getValue ( ) * ( - MathUtil.degToRad ( mc.player.rotationPitch ) ) ) * mc.player.movementInput.moveForward;
        }

        if ( mc.gameSettings.keyBindJump.isKeyDown ( ) ) {
            mc.player.motionY += speed.getValue ( );
        }

        if ( mc.gameSettings.keyBindSneak.isKeyDown ( ) ) {
            mc.player.motionY -= speed.getValue ( );
        }
    }

    @Override
    public
    void onLogout ( ) {
        if ( disable.getValue ( ) ) {
            this.disable ( );
        }
    }

    @SubscribeEvent
    public
    void onPacketSend ( PacketEvent.Send event ) {
        if ( event.getStage ( ) == 0 && ( event.getPacket ( ) instanceof CPacketPlayer || event.getPacket ( ) instanceof CPacketInput ) ) {
            event.setCanceled ( true );
        }
    }

    @SubscribeEvent
    public
    void onPush ( PushEvent event ) {
        if ( event.getStage ( ) == 1 ) {
            event.setCanceled ( true );
        }
    }
}
