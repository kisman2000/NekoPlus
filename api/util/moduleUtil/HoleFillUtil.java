package me.vaimok.nekoplus.api.util.moduleUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Arrays;
import java.util.List;

public
class HoleFillUtil {
    public static final List < Block > blackList = Arrays.asList ( new Block[]{
            Blocks.ENDER_CHEST , (Block) Blocks.CHEST , Blocks.TRAPPED_CHEST , Blocks.CRAFTING_TABLE , Blocks.ANVIL , Blocks.BREWING_STAND , (Block) Blocks.HOPPER , Blocks.DROPPER , Blocks.DISPENSER , Blocks.TRAPDOOR ,
            Blocks.ENCHANTING_TABLE} );

    public static final List < Block > shulkerList = Arrays.asList ( new Block[]{
            Blocks.WHITE_SHULKER_BOX , Blocks.ORANGE_SHULKER_BOX , Blocks.MAGENTA_SHULKER_BOX , Blocks.LIGHT_BLUE_SHULKER_BOX , Blocks.YELLOW_SHULKER_BOX , Blocks.LIME_SHULKER_BOX , Blocks.PINK_SHULKER_BOX , Blocks.GRAY_SHULKER_BOX , Blocks.SILVER_SHULKER_BOX , Blocks.CYAN_SHULKER_BOX ,
            Blocks.PURPLE_SHULKER_BOX , Blocks.BLUE_SHULKER_BOX , Blocks.BROWN_SHULKER_BOX , Blocks.GREEN_SHULKER_BOX , Blocks.RED_SHULKER_BOX , Blocks.BLACK_SHULKER_BOX} );

    private static final Minecraft mc = Minecraft.getMinecraft ( );
    private static final Entity player = mc.player;
    public static FMLCommonHandler fmlHandler = FMLCommonHandler.instance ( );

    public static
    void placeBlockScaffold ( BlockPos pos ) {
        Vec3d eyesPos = new Vec3d ( player.posX , player.posY + player.getEyeHeight ( ) , player.posZ );
        for (EnumFacing side : EnumFacing.values ( )) {
            BlockPos neighbor = pos.offset ( side );
            EnumFacing side2 = side.getOpposite ( );
            if ( canBeClicked ( neighbor ) ) {
                Vec3d hitVec = ( new Vec3d ( (Vec3i) neighbor ) ).add ( 0.5D , 0.5D , 0.5D ).add ( ( new Vec3d ( side2.getDirectionVec ( ) ) ).scale ( 0.5D ) );
                if ( eyesPos.squareDistanceTo ( hitVec ) <= 18.0625D ) {
                    faceVectorPacketInstant ( hitVec );
                    processRightClickBlock ( neighbor , side2 , hitVec );
                    mc.player.swingArm ( EnumHand.MAIN_HAND );
                    mc.rightClickDelayTimer = 4;

                    return;
                }
            }
        }
    }

    private static
    float[] getLegitRotations ( Vec3d vec ) {
        Vec3d eyesPos = getEyesPos ( );
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt ( diffX * diffX + diffZ * diffZ );
        float yaw = (float) Math.toDegrees ( Math.atan2 ( diffZ , diffX ) ) - 90.0F;
        float pitch = (float) - Math.toDegrees ( Math.atan2 ( diffY , diffXZ ) );
        return new float[]{( mc.player.rotationYaw + MathHelper.wrapDegrees ( yaw - mc.player.rotationYaw ) ) , mc.player.rotationPitch + MathHelper.wrapDegrees ( pitch - mc.player.rotationPitch )}; // test
    }

    private static
    Vec3d getEyesPos ( ) {
        return new Vec3d ( ( mc.player ).posX ,
                ( mc.player ).posY + mc.player.getEyeHeight ( ) ,
                ( mc.player ).posZ );
    }

    public static
    void faceVectorPacketInstant ( Vec3d vec ) {
        float[] rotations = getLegitRotations ( vec );
        ( mc.player ).connection.sendPacket ( (Packet) new CPacketPlayer.Rotation ( rotations[0] , rotations[1] ,
                ( mc.player ).onGround ) );
    }

    private static
    void processRightClickBlock ( BlockPos pos , EnumFacing side , Vec3d hitVec ) {
        getPlayerController ( ).processRightClickBlock ( mc.player , mc.world , pos , side , hitVec , EnumHand.MAIN_HAND );
    }

    public static
    boolean canBeClicked ( BlockPos pos ) {
        return getBlock ( pos ).canCollideCheck ( getState ( pos ) , false );
    }

    private static
    Block getBlock ( BlockPos pos ) {
        return getState ( pos ).getBlock ( );
    }

    private static
    PlayerControllerMP getPlayerController ( ) {
        return ( Minecraft.getMinecraft ( ) ).playerController;
    }

    private static
    IBlockState getState ( BlockPos pos ) {
        return mc.world.getBlockState ( pos );
    }

    public static
    boolean checkForNeighbours ( BlockPos blockPos ) {
        if ( ! hasNeighbour ( blockPos ) ) {
            for (EnumFacing side : EnumFacing.values ( )) {
                BlockPos neighbour = blockPos.offset ( side );
                if ( hasNeighbour ( neighbour ) )
                    return true;
            }
            return false;
        }
        return true;
    }

    public static
    EnumFacing getPlaceableSide ( BlockPos pos ) {
        for (EnumFacing side : EnumFacing.values ( )) {
            BlockPos neighbour = pos.offset ( side );
            if ( mc.world.getBlockState ( neighbour ).getBlock ( ).canCollideCheck ( mc.world.getBlockState ( neighbour ) , false ) ) {
                IBlockState blockState = mc.world.getBlockState ( neighbour );
                if ( ! blockState.getMaterial ( ).isReplaceable ( ) )
                    return side;
            }
        }
        return null;
    }

    public static
    boolean hasNeighbour ( BlockPos blockPos ) {
        for (EnumFacing side : EnumFacing.values ( )) {
            BlockPos neighbour = blockPos.offset ( side );
            if ( ! mc.world.getBlockState ( neighbour ).getMaterial ( ).isReplaceable ( ) )
                return true;
        }
        return false;
    }
}
