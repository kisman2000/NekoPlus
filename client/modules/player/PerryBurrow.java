package me.vaimok.nekoplus.client.modules.player;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.event.events.UpdateWalkingPlayerEvent;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.BlockUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.InventoryUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.MathUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PerryBurrow extends Module {
    private static me.vaimok.nekoplus.client.modules.player.PerryBurrow INSTANCE;

    private final Setting < Mode > mode = this.register ( new Setting < Mode > ( "Mode" , Mode.OBSIDIAN ) );
    private final Setting < Boolean > smartTp = this.register ( new Setting < Boolean > ( "SmartTP" , true ) );
    private final Setting < Integer > tpMin = this.register ( new Setting < Integer > ( "TPMin" , 3 , 3 , 10 , v -> this.smartTp.getValue ( ) ) );
    private final Setting < Integer > tpMax = this.register ( new Setting < Integer > ( "TPMax" , 25 , 10 , 40 , v -> this.smartTp.getValue ( ) ) );
    private final Setting < Boolean > noVoid = this.register ( new Setting < Boolean > ( "NoVoid" , true , v -> this.smartTp.getValue ( ) ) );
    private final Setting < Integer > tpHeight = this.register ( new Setting < Integer > ( "TPHeight" , 2 , - 40 , 40 , v -> ! this.smartTp.getValue ( ) ) );
    private final Setting < Boolean > keepInside = this.register ( new Setting < Boolean > ( "Center" , true ) );
    private final Setting < Boolean > rotate = this.register ( new Setting < Boolean > ( "Rotate" , false ) );
    private final Setting < Boolean > sneaking = this.register ( new Setting < Boolean > ( "Sneak" , false ) );
    private final Setting < Boolean > offground = this.register ( new Setting < Boolean > ( "Offground" , false ) );
    private final Setting < Boolean > chat = this.register ( new Setting < Boolean > ( "Chat Msgs" , true ) );
    private final Setting < Boolean > tpdebug = this.register ( new Setting < Boolean > ( "Debug" , false , v -> this.chat.getValue ( ) && this.smartTp.getValue ( ) ) );

    private BlockPos burrowPos;
    private int lastBlock;
    private int blockSlot;
    private Class block;
    private String name;
    private Vec3d back;

    public PerryBurrow( ) {
        super ( "PerryBurrow" , "gay" , Category.PLAYER , true , false , false );
        INSTANCE = this;
    }

    public static me.vaimok.nekoplus.client.modules.player.PerryBurrow getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new me.vaimok.nekoplus.client.modules.player.PerryBurrow( );
        }
        return INSTANCE;
    }


    @Override
    public
    void onEnable ( ) {
        this.burrowPos = new BlockPos ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posX , Math.ceil ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posY ) , me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posZ );
        this.blockSlot = this.findBlockSlot ( );
        this.lastBlock = me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.inventory.currentItem;

        if ( ! doChecks ( ) || this.blockSlot == - 1 ) {
            this.disable ( );
            return;
        }

        if ( this.keepInside.getValue ( ) ) {
            double x = me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posX - Math.floor ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posX );
            double z = me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posZ - Math.floor ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posZ );

            if ( x <= 0.3 || x >= 0.7 ) {
                x = ( x > 0.5 ? 0.69 : 0.31 );
            }

            if ( z < 0.3 || z > 0.7 ) {
                z = ( z > 0.5 ? 0.69 : 0.31 );
            }

            me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( Math.floor ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posX ) + x , me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posY , Math.floor ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posZ ) + z , me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.onGround ) );
            me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.setPosition ( Math.floor ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posX ) + x , me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posY , Math.floor ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posZ ) + z );
            // no fucking clue how this worked i made it drunk
        }

        me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( mc.player.posX , mc.player.posY + 0.41999998688698D , mc.player.posZ , ! this.offground.getValue ( ) ) );
        me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( mc.player.posX , mc.player.posY + 0.7531999805211997D , mc.player.posZ , ! this.offground.getValue ( ) ) );
        me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( mc.player.posX , mc.player.posY + 1.00133597911214D , mc.player.posZ , ! this.offground.getValue ( ) ) );
        me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( mc.player.posX , mc.player.posY + 1.16610926093821D , mc.player.posZ , ! this.offground.getValue ( ) ) );
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( UpdateWalkingPlayerEvent event ) {
        if ( event.getStage ( ) != 0 ) {
            return;
        }

        if ( this.rotate.getValue ( ) ) {
            float[] angle = MathUtil.calcAngle ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) , new Vec3d ( (float) burrowPos.getX ( ) + 0.5f , (float) burrowPos.getY ( ) + 0.5f , (float) burrowPos.getZ ( ) + 0.5f ) );
            nekoplus.rotationManager.setPlayerRotations ( angle[0] , angle[1] );
        }

        InventoryUtil.switchToHotbarSlot ( this.blockSlot , false );
        BlockUtil.placeBlock ( this.burrowPos , this.blockSlot == - 2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND , false , true , this.sneaking.getValue ( ) );
        InventoryUtil.switchToHotbarSlot ( this.lastBlock , false );

        me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posX , ( this.smartTp.getValue ( ) ? this.adaptiveTpHeight ( ) : this.tpHeight.getValue ( ) + me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posY ) , me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.posZ , ! this.offground.getValue ( ) ) );

        this.disable ( );
    }

    private
    int findBlockSlot ( ) {

        // theres prob an easier way to do this but im retarded
        // phobos uses "this." everywhere so im just gonna keep doing it even though its kinda pointless
        switch (this.mode.getValue ( )) {
            case ECHEST:
                this.block = BlockEnderChest.class;
                this.name = "Ender Chests";
                break;
            case OBSIDIAN:
                this.block = BlockObsidian.class;
                this.name = "Obsidian";
                break;
            case SOULSAND:
                this.block = BlockSoulSand.class;
                this.name = "Soul Sand";
                break;
        }

        int slot = InventoryUtil.findHotbarBlock ( this.block );
        if ( slot == - 1 ) {
            if ( InventoryUtil.isBlock ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player.getHeldItemOffhand ( ).getItem ( ) , this.block ) ) {
                return - 2;
            } else {
                if ( this.chat.getValue ( ) )
                    Command.sendMessage ( "\u00A77" + this.displayName.getValue ( ) + ":\u00A7c No " + this.name + " to use" );
            }
        }

        return slot;
    }

    private
    int adaptiveTpHeight ( ) {

        int airblock = ( this.noVoid.getValue ( ) && this.tpMax.getValue ( ) * - 1 + this.burrowPos.getY ( ) < 0 ? this.burrowPos.getY ( ) * - 1 : this.tpMax.getValue ( ) * - 1 );

        while ( airblock < this.tpMax.getValue ( ) ) {
            if ( Math.abs ( airblock ) < this.tpMin.getValue ( ) || ! me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.world.isAirBlock ( this.burrowPos.offset ( EnumFacing.UP , airblock ) ) || ! me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.world.isAirBlock ( this.burrowPos.offset ( EnumFacing.UP , airblock + 1 ) ) ) {
                airblock++;
            } else {
                if ( this.tpdebug.getValue ( ) ) Command.sendMessage ( Integer.toString ( airblock ) );
                return this.burrowPos.getY ( ) + airblock;
            }
        }

        return 69420; // if there isn't any room
    }

    private
    boolean
    doChecks ( ) {

        if ( ! me.vaimok.nekoplus.client.modules.player.PerryBurrow.fullNullCheck ( ) ) {

            if ( this.smartTp.getValue ( ) ) {
                if ( adaptiveTpHeight ( ) == 69420 ) { // check if there is room to tp
                    if ( this.chat.getValue ( ) )
                        Command.sendMessage ( "\u00A77" + this.displayName.getValue ( ) + ":\u00A7c Not enough room" );
                    return false;
                }
            }

            if ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.world.getBlockState ( this.burrowPos ).getBlock ( ).equals ( Blocks.OBSIDIAN ) ) { // is the player already burrowed
                return false;
            }

            if ( ! me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.world.isAirBlock ( this.burrowPos.offset ( EnumFacing.UP , 2 ) ) ) { // is the player trapped
                if ( this.chat.getValue ( ) )
                    Command.sendMessage ( "\u00A77" + this.displayName.getValue ( ) + ":\u00A7c Not enough room" );
                return false;
            }

            for (final Entity entity : me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.world.loadedEntityList) {
                if ( ! ( entity instanceof EntityItem ) && ! entity.equals ( me.vaimok.nekoplus.client.modules.player.PerryBurrow.mc.player ) ) {
                    if ( new AxisAlignedBB ( burrowPos ).intersects ( entity.getEntityBoundingBox ( ) ) ) { // is there another player in the hole
                        if ( this.chat.getValue ( ) )
                            Command.sendMessage ( "\u00A77" + this.displayName.getValue ( ) + ":\u00A7c Not enough room" );
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    public
    enum Mode {
        OBSIDIAN,
        ECHEST,
        SOULSAND

    }
}