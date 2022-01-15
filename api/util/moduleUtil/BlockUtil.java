package me.vaimok.nekoplus.api.util.moduleUtil;

import com.google.common.util.concurrent.AtomicDouble;
import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public
class BlockUtil implements Util {

    public static final List < Block > blackList = Arrays.asList (
            Blocks.ENDER_CHEST ,
            Blocks.CHEST ,
            Blocks.TRAPPED_CHEST ,
            Blocks.CRAFTING_TABLE ,
            Blocks.ANVIL ,
            Blocks.BREWING_STAND ,
            Blocks.HOPPER ,
            Blocks.DROPPER ,
            Blocks.DISPENSER ,
            Blocks.TRAPDOOR ,
            Blocks.ENCHANTING_TABLE
    );
    public static final List < Block > shulkerList = Arrays.asList (
            Blocks.WHITE_SHULKER_BOX ,
            Blocks.ORANGE_SHULKER_BOX ,
            Blocks.MAGENTA_SHULKER_BOX ,
            Blocks.LIGHT_BLUE_SHULKER_BOX ,
            Blocks.YELLOW_SHULKER_BOX ,
            Blocks.LIME_SHULKER_BOX ,
            Blocks.PINK_SHULKER_BOX ,
            Blocks.GRAY_SHULKER_BOX ,
            Blocks.SILVER_SHULKER_BOX ,
            Blocks.CYAN_SHULKER_BOX ,
            Blocks.PURPLE_SHULKER_BOX ,
            Blocks.BLUE_SHULKER_BOX ,
            Blocks.BROWN_SHULKER_BOX ,
            Blocks.GREEN_SHULKER_BOX ,
            Blocks.RED_SHULKER_BOX ,
            Blocks.BLACK_SHULKER_BOX
    );
    public static List < Block > unSolidBlocks = Arrays.asList (
            Blocks.FLOWING_LAVA ,
            Blocks.FLOWER_POT ,
            Blocks.SNOW ,
            Blocks.CARPET ,
            Blocks.END_ROD ,
            Blocks.SKULL ,
            Blocks.FLOWER_POT ,
            Blocks.TRIPWIRE ,
            Blocks.TRIPWIRE_HOOK ,
            Blocks.WOODEN_BUTTON ,
            Blocks.LEVER ,
            Blocks.STONE_BUTTON ,
            Blocks.LADDER ,
            Blocks.UNPOWERED_COMPARATOR ,
            Blocks.POWERED_COMPARATOR ,
            Blocks.UNPOWERED_REPEATER ,
            Blocks.POWERED_REPEATER ,
            Blocks.UNLIT_REDSTONE_TORCH ,
            Blocks.REDSTONE_TORCH ,
            Blocks.REDSTONE_WIRE ,
            Blocks.AIR ,
            Blocks.PORTAL ,
            Blocks.END_PORTAL ,
            Blocks.WATER ,
            Blocks.FLOWING_WATER ,
            Blocks.LAVA ,
            Blocks.FLOWING_LAVA ,
            Blocks.SAPLING ,
            Blocks.RED_FLOWER ,
            Blocks.YELLOW_FLOWER ,
            Blocks.BROWN_MUSHROOM ,
            Blocks.RED_MUSHROOM ,
            Blocks.WHEAT ,
            Blocks.CARROTS ,
            Blocks.POTATOES ,
            Blocks.BEETROOTS ,
            Blocks.REEDS ,
            Blocks.PUMPKIN_STEM ,
            Blocks.MELON_STEM ,
            Blocks.WATERLILY ,
            Blocks.NETHER_WART ,
            Blocks.COCOA ,
            Blocks.CHORUS_FLOWER ,
            Blocks.CHORUS_PLANT ,
            Blocks.TALLGRASS ,
            Blocks.DEADBUSH ,
            Blocks.VINE ,
            Blocks.FIRE ,
            Blocks.RAIL ,
            Blocks.ACTIVATOR_RAIL ,
            Blocks.DETECTOR_RAIL ,
            Blocks.GOLDEN_RAIL ,
            Blocks.TORCH
    );

    public static
    void placeBlock ( BlockPos pos ) {
        Vec3d eyesPos = new Vec3d ( mc.player.posX , mc.player.posY + mc.player.getEyeHeight ( ) , mc.player.posZ );
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
    Vec3d getEyesPos ( ) {
        return new Vec3d ( ( mc.player ).posX ,
                ( mc.player ).posY + mc.player.getEyeHeight ( ) ,
                ( mc.player ).posZ );
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
    void processRightClickBlock ( BlockPos pos , EnumFacing side , Vec3d hitVec ) {
        mc.playerController.processRightClickBlock ( mc.player , mc.world , pos , side , hitVec , EnumHand.MAIN_HAND );
    }

    public static
    void faceVectorPacketInstant ( Vec3d vec ) {
        float[] rotations = getLegitRotations ( vec );
        ( mc.player ).connection.sendPacket ( (Packet) new CPacketPlayer.Rotation ( rotations[0] , rotations[1] ,
                ( mc.player ).onGround ) );
    }

    public static
    void placeBlockScaffold ( final BlockPos pos ) {
        final Vec3d eyesPos = new Vec3d ( BlockUtil.mc.player.posX , BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight ( ) , BlockUtil.mc.player.posZ );
        for (final EnumFacing side : EnumFacing.values ( )) {
            final BlockPos neighbor = pos.offset ( side );
            final EnumFacing side2 = side.getOpposite ( );
            final Vec3d hitVec;
            if ( canBeClicked ( neighbor ) && eyesPos.squareDistanceTo ( hitVec = new Vec3d ( neighbor ).add ( 0.5 , 0.5 , 0.5 ).add ( new Vec3d ( side2.getDirectionVec ( ) ).scale ( 0.5 ) ) ) <= 18.0625 ) {
                faceVectorPacketInstant ( hitVec );
                processRightClickBlock ( neighbor , side2 , hitVec );
                BlockUtil.mc.player.swingArm ( EnumHand.MAIN_HAND );
                BlockUtil.mc.rightClickDelayTimer = 4;
                return;
            }
        }
    }

    public static
    boolean isIntercepted ( BlockPos pos ) {
        for (Entity entity : mc.world.loadedEntityList) {
            if ( ! new AxisAlignedBB ( pos ).intersects ( entity.getEntityBoundingBox ( ) ) ) continue;
            return true;
        }
        return false;
    }

    public static
    void placeBlock ( BlockPos pos , int slot ) {
        if ( slot == - 1 ) {
            return;
        }
        int prev = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slot;
        placeBlock ( pos );
        mc.player.inventory.currentItem = prev;
    }

    public static
    List < BlockPos > getBlockSphere ( float breakRange , Class clazz ) {
        NonNullList < BlockPos > positions = NonNullList.create ( );
        positions.addAll ( BlockUtil.getSphere ( EntityUtil.getPlayerPos ( mc.player ) , breakRange , (int) breakRange , false , true , 0 ).stream ( ).filter (pos -> clazz.isInstance ( mc.world.getBlockState ( pos ).getBlock ( ) ) ).collect ( Collectors.toList ( ) ) );
        return positions;
    }

    public static
    List < EnumFacing > getPossibleSides ( BlockPos pos ) {
        List < EnumFacing > facings = new ArrayList <> ( );
        for (EnumFacing side : EnumFacing.values ( )) {
            BlockPos neighbour = pos.offset ( side );
            if ( mc.world.getBlockState ( neighbour ).getBlock ( ).canCollideCheck ( mc.world.getBlockState ( neighbour ) , false ) ) {
                IBlockState blockState = mc.world.getBlockState ( neighbour );
                if ( ! blockState.getMaterial ( ).isReplaceable ( ) ) {
                    facings.add ( side );
                }
            }
        }
        return facings;
    }

    public static
    EnumFacing getFirstFacing ( BlockPos pos ) {
        for (EnumFacing facing : getPossibleSides ( pos )) {
            return facing;
        }
        return null;
    }

    public static
    EnumFacing getRayTraceFacing ( BlockPos pos ) {
        RayTraceResult result = mc.world.rayTraceBlocks ( new Vec3d ( mc.player.posX , mc.player.posY + mc.player.getEyeHeight ( ) , mc.player.posZ ) , new Vec3d ( pos.getX ( ) + .5 , pos.getX ( ) - .5d , pos.getX ( ) + .5 ) );
        if ( result == null || result.sideHit == null ) {
            return EnumFacing.UP;
        }
        return result.sideHit;
    }

    public static
    int isPositionPlaceable ( BlockPos pos , boolean rayTrace ) {
        return isPositionPlaceable ( pos , rayTrace , true );
    }

    public static
    int isPositionPlaceable ( BlockPos pos , boolean rayTrace , boolean entityCheck ) {
        Block block = mc.world.getBlockState ( pos ).getBlock ( );
        if ( ! ( block instanceof BlockAir ) && ! ( block instanceof BlockLiquid ) && ! ( block instanceof BlockTallGrass ) && ! ( block instanceof BlockFire ) && ! ( block instanceof BlockDeadBush ) && ! ( block instanceof BlockSnow ) ) {
            return 0;
        }

        if ( ! rayTracePlaceCheck ( pos , rayTrace , 0.0f ) ) {
            return - 1;
        }

        if ( entityCheck ) {
            for (Entity entity : mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( pos ) )) {
                if ( ! ( entity instanceof EntityItem ) && ! ( entity instanceof EntityXPOrb ) ) {
                    return 1;
                }
            }
        }

        for (EnumFacing side : getPossibleSides ( pos )) {
            if ( canBeClicked ( pos.offset ( side ) ) ) {
                return 3;
            }
        }

        return 2;
    }

    public static
    void rightClickBlock ( BlockPos pos , Vec3d vec , EnumHand hand , EnumFacing direction , boolean packet ) {
        if ( packet ) {
            float f = (float) ( vec.x - (double) pos.getX ( ) );
            float f1 = (float) ( vec.y - (double) pos.getY ( ) );
            float f2 = (float) ( vec.z - (double) pos.getZ ( ) );
            mc.player.connection.sendPacket ( new CPacketPlayerTryUseItemOnBlock ( pos , direction , hand , f , f1 , f2 ) );
        } else {
            mc.playerController.processRightClickBlock ( mc.player , mc.world , pos , direction , vec , hand );
        }
        mc.player.swingArm ( EnumHand.MAIN_HAND );
        mc.rightClickDelayTimer = 4; //?
    }

    public static
    void rightClickBlockLegit ( BlockPos pos , float range , boolean rotate , EnumHand hand , AtomicDouble Yaw , AtomicDouble Pitch , AtomicBoolean rotating ) {
        Vec3d eyesPos = RotationUtil.getEyesPos ( );
        Vec3d posVec = new Vec3d ( pos ).add ( 0.5 , 0.5 , 0.5 );
        double distanceSqPosVec = eyesPos.squareDistanceTo ( posVec );
        for (EnumFacing side : EnumFacing.values ( )) {
            Vec3d hitVec = posVec.add ( new Vec3d ( side.getDirectionVec ( ) ).scale ( 0.5 ) );
            double distanceSqHitVec = eyesPos.squareDistanceTo ( hitVec );

            if ( distanceSqHitVec > MathUtil.square ( range ) ) {
                continue;
            }

            if ( distanceSqHitVec >= distanceSqPosVec ) {
                continue;
            }

            if ( mc.world.rayTraceBlocks ( eyesPos , hitVec , false , true , false ) != null ) {
                continue;
            }

            if ( rotate ) {
                float[] rotations = RotationUtil.getLegitRotations ( hitVec );
                Yaw.set ( rotations[0] );
                Pitch.set ( rotations[1] );
                rotating.set ( true );
            }

            mc.playerController.processRightClickBlock ( mc.player , mc.world , pos , side , hitVec , hand );
            mc.player.swingArm ( hand );
            mc.rightClickDelayTimer = 4;
            break;
        }
    }

    public static
    boolean placeBlock ( BlockPos pos , EnumHand hand , boolean rotate , boolean packet , boolean isSneaking ) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing ( pos );
        if ( side == null ) {
            return isSneaking;
        }

        BlockPos neighbour = pos.offset ( side );
        EnumFacing opposite = side.getOpposite ( );

        Vec3d hitVec = new Vec3d ( neighbour ).add ( 0.5 , 0.5 , 0.5 ).add ( new Vec3d ( opposite.getDirectionVec ( ) ).scale ( 0.5 ) );
        Block neighbourBlock = mc.world.getBlockState ( neighbour ).getBlock ( );

        if ( ! mc.player.isSneaking ( ) && ( blackList.contains ( neighbourBlock ) || shulkerList.contains ( neighbourBlock ) ) ) {
            mc.player.connection.sendPacket ( new CPacketEntityAction ( mc.player , CPacketEntityAction.Action.START_SNEAKING ) );
            mc.player.setSneaking ( true );
            sneaking = true;
        }

        if ( rotate ) {
            RotationUtil.faceVector ( hitVec , true );
        }

        rightClickBlock ( neighbour , hitVec , hand , opposite , packet );
        mc.player.swingArm ( EnumHand.MAIN_HAND );
        mc.rightClickDelayTimer = 4; //?
        return sneaking || isSneaking;
    }

    public static
    boolean placeBlockSmartRotate ( BlockPos pos , EnumHand hand , boolean rotate , boolean packet , boolean isSneaking ) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing ( pos );
        Command.sendMessage ( side.toString ( ) );
        if ( side == null ) {
            return isSneaking;
        }

        BlockPos neighbour = pos.offset ( side );
        EnumFacing opposite = side.getOpposite ( );

        Vec3d hitVec = new Vec3d ( neighbour ).add ( 0.5 , 0.5 , 0.5 ).add ( new Vec3d ( opposite.getDirectionVec ( ) ).scale ( 0.5 ) );
        Block neighbourBlock = mc.world.getBlockState ( neighbour ).getBlock ( );

        if ( ! mc.player.isSneaking ( ) && ( blackList.contains ( neighbourBlock ) || shulkerList.contains ( neighbourBlock ) ) ) {
            mc.player.connection.sendPacket ( new CPacketEntityAction ( mc.player , CPacketEntityAction.Action.START_SNEAKING ) );
            sneaking = true;
        }

        if ( rotate ) {
            nekoplus.rotationManager.lookAtVec3d ( hitVec );
        }

        rightClickBlock ( neighbour , hitVec , hand , opposite , packet );
        mc.player.swingArm ( EnumHand.MAIN_HAND );
        mc.rightClickDelayTimer = 4; //?
        return sneaking || isSneaking;
    }

    public static
    void placeBlockStopSneaking ( BlockPos pos , EnumHand hand , boolean rotate , boolean packet , boolean isSneaking ) {
        boolean sneaking = placeBlockSmartRotate ( pos , hand , rotate , packet , isSneaking );
        if ( ! isSneaking && sneaking ) {
            mc.player.connection.sendPacket ( new CPacketEntityAction ( mc.player , CPacketEntityAction.Action.STOP_SNEAKING ) );
        }
    }

    public static
    Vec3d[] getHelpingBlocks ( Vec3d vec3d ) {
        return new Vec3d[]{
                new Vec3d ( vec3d.x , vec3d.y - 1 , vec3d.z ) ,
                new Vec3d ( vec3d.x != 0 ? vec3d.x * 2 : vec3d.x , vec3d.y , vec3d.x != 0 ? vec3d.z : vec3d.z * 2 ) ,
                new Vec3d ( vec3d.x == 0 ? vec3d.x + 1 : vec3d.x , vec3d.y , vec3d.x == 0 ? vec3d.z : vec3d.z + 1 ) ,
                new Vec3d ( vec3d.x == 0 ? vec3d.x - 1 : vec3d.x , vec3d.y , vec3d.x == 0 ? vec3d.z : vec3d.z - 1 ) ,
                new Vec3d ( vec3d.x , vec3d.y + 1 , vec3d.z )
        };
    }

    public static
    List < BlockPos > possiblePlacePositions ( float placeRange ) {
        NonNullList < BlockPos > positions = NonNullList.create ( );
        positions.addAll ( getSphere ( EntityUtil.getPlayerPos ( mc.player ) , placeRange , (int) placeRange , false , true , 0 ).stream ( ).filter ( BlockUtil::canPlaceCrystal ).collect ( Collectors.toList ( ) ) );
        return positions;
    }

    public static
    List < BlockPos > possiblePlacePositions ( final float placeRange , final boolean specialEntityCheck , final boolean oneDot15 ) {
        final NonNullList < BlockPos > positions = NonNullList.create ( );
        positions.addAll ( (Collection) getSphere ( EntityUtil.getPlayerPos ( (EntityPlayer) BlockUtil.mc.player ) , placeRange , (int) placeRange , false , true , 0 ).stream ( ).filter ( pos -> canPlaceCrystal ( pos , specialEntityCheck , oneDot15 ) ).collect ( Collectors.toList ( ) ) );
        return (List < BlockPos >) positions;
    }

    public static
    List < BlockPos > getSphere ( float radius , boolean ignoreAir ) {
        ArrayList < BlockPos > sphere = new ArrayList < BlockPos > ( );
        BlockPos pos = new BlockPos ( BlockUtil.mc.player.getPositionVector ( ) );
        int posX = pos.getX ( );
        int posY = pos.getY ( );
        int posZ = pos.getZ ( );
        int radiuss = (int) radius;
        int x = posX - radiuss;
        while ( (float) x <= (float) posX + radius ) {
            int z = posZ - radiuss;
            while ( (float) z <= (float) posZ + radius ) {
                int y = posY - radiuss;
                while ( (float) y < (float) posY + radius ) {
                    BlockPos position;
                    double dist = ( posX - x ) * ( posX - x ) + ( posZ - z ) * ( posZ - z ) + ( posY - y ) * ( posY - y );
                    if ( dist < (double) ( radius * radius ) && ( BlockUtil.mc.world.getBlockState ( position = new BlockPos ( x , y , z ) ).getBlock ( ) != Blocks.AIR || ! ignoreAir ) ) {
                        sphere.add ( position );
                    }
                    ++ y;
                }
                ++ z;
            }
            ++ x;
        }
        return sphere;
    }

    public static
    List < BlockPos > getSphere ( BlockPos pos , float r , int h , boolean hollow , boolean sphere , int plus_y ) {
        List < BlockPos > circleblocks = new ArrayList <> ( );
        int cx = pos.getX ( );
        int cy = pos.getY ( );
        int cz = pos.getZ ( );
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = ( sphere ? cy - (int) r : cy ); y < ( sphere ? cy + r : cy + h ); y++) {
                    double dist = ( cx - x ) * ( cx - x ) + ( cz - z ) * ( cz - z ) + ( sphere ? ( cy - y ) * ( cy - y ) : 0 );
                    if ( dist < r * r && ! ( hollow && dist < ( r - 1 ) * ( r - 1 ) ) ) {
                        BlockPos l = new BlockPos ( x , y + plus_y , z );
                        circleblocks.add ( l );
                    }
                }
            }
        }
        return circleblocks;
    }

    public static
    boolean canPlaceCrystal ( BlockPos blockPos ) {
        BlockPos boost = blockPos.add ( 0 , 1 , 0 );
        BlockPos boost2 = blockPos.add ( 0 , 2 , 0 );
        try {
            return ( mc.world.getBlockState ( blockPos ).getBlock ( ) == Blocks.BEDROCK
                    || mc.world.getBlockState ( blockPos ).getBlock ( ) == Blocks.OBSIDIAN )
                    && mc.world.getBlockState ( boost ).getBlock ( ) == Blocks.AIR
                    && mc.world.getBlockState ( boost2 ).getBlock ( ) == Blocks.AIR
                    && mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost ) ).isEmpty ( )
                    && mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost2 ) ).isEmpty ( );
        } catch ( Exception e ) {
            return false;
        }
    }

    public static
    boolean canPlaceCrystal ( final BlockPos blockPos , final boolean specialEntityCheck , final boolean oneDot15 ) {
        final BlockPos boost = blockPos.add ( 0 , 1 , 0 );
        final BlockPos boost2 = blockPos.add ( 0 , 2 , 0 );
        try {
            if ( BlockUtil.mc.world.getBlockState ( blockPos ).getBlock ( ) != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState ( blockPos ).getBlock ( ) != Blocks.OBSIDIAN ) {
                return false;
            }
            if ( ( BlockUtil.mc.world.getBlockState ( boost ).getBlock ( ) != Blocks.AIR || BlockUtil.mc.world.getBlockState ( boost2 ).getBlock ( ) != Blocks.AIR ) && ! oneDot15 ) {
                return false;
            }
            if ( ! specialEntityCheck ) {
                return BlockUtil.mc.world.getEntitiesWithinAABB ( (Class) Entity.class , new AxisAlignedBB ( boost ) ).isEmpty ( ) && ( oneDot15 || BlockUtil.mc.world.getEntitiesWithinAABB ( (Class) Entity.class , new AxisAlignedBB ( boost2 ) ).isEmpty ( ) );
            }
            for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost ) )) {
                if ( ! ( entity instanceof EntityEnderCrystal ) ) {
                    return false;
                }
            }
            if ( ! oneDot15 ) {
                for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost2 ) )) {
                    if ( ! ( entity instanceof EntityEnderCrystal ) ) {
                        return false;
                    }
                }
            }
        } catch ( Exception ignored ) {
            return false;
        }
        return true;
    }

    public static
    List < BlockPos > possiblePlacePositions ( float placeRange , boolean specialEntityCheck ) {
        NonNullList < BlockPos > positions = NonNullList.create ( );
        positions.addAll ( getSphere ( EntityUtil.getPlayerPos ( mc.player ) , placeRange , (int) placeRange , false , true , 0 ).stream ( ).filter ( pos -> canPlaceCrystal ( pos , specialEntityCheck ) ).collect ( Collectors.toList ( ) ) );
        return positions;
    }

    public static
    boolean canPlaceCrystal ( BlockPos blockPos , boolean specialEntityCheck ) {
        BlockPos boost = blockPos.add ( 0 , 1 , 0 );
        BlockPos boost2 = blockPos.add ( 0 , 2 , 0 );
        try {
            if ( mc.world.getBlockState ( blockPos ).getBlock ( ) != Blocks.BEDROCK && mc.world.getBlockState ( blockPos ).getBlock ( ) != Blocks.OBSIDIAN ) {
                return false;
            }

            if ( ! ( mc.world.getBlockState ( boost ).getBlock ( ) == Blocks.AIR && mc.world.getBlockState ( boost2 ).getBlock ( ) == Blocks.AIR ) ) {
                return false;
            }

            if ( specialEntityCheck ) {
                for (Entity entity : mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost ) )) {
                    if ( ! ( entity instanceof EntityEnderCrystal ) ) {
                        return false;
                    }
                }

                for (Entity entity : mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost2 ) )) {
                    if ( ! ( entity instanceof EntityEnderCrystal ) ) {
                        return false;
                    }
                }
            } else {
                return mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost ) ).isEmpty ( )
                        && mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( boost2 ) ).isEmpty ( );
            }
        } catch ( Exception ignored ) {
            return false;
        }

        return true;
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
    IBlockState getState ( BlockPos pos ) {
        return mc.world.getBlockState ( pos );
    }

    public static
    boolean isBlockAboveEntitySolid ( Entity entity ) {
        if ( entity != null ) {
            final BlockPos pos = new BlockPos ( entity.posX , entity.posY + 2.0 , entity.posZ );
            return isBlockSolid ( pos );
        }
        return false;
    }

    public static
    void debugPos ( String message , BlockPos pos ) {
        Command.sendMessage ( message + pos.getX ( ) + "x, " + pos.getY ( ) + "y, " + pos.getZ ( ) + "z" );
    }

    public static
    void placeCrystalOnBlock ( BlockPos pos , EnumHand hand ) {
        RayTraceResult result = mc.world.rayTraceBlocks ( new Vec3d ( mc.player.posX , mc.player.posY + mc.player.getEyeHeight ( ) , mc.player.posZ ) , new Vec3d ( pos.getX ( ) + .5 , pos.getY ( ) - .5d , pos.getZ ( ) + .5 ) );
        EnumFacing facing = ( result == null || result.sideHit == null ) ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket ( new CPacketPlayerTryUseItemOnBlock ( pos , facing , hand , 0 , 0 , 0 ) );
    }

    public static
    void placeCrystalOnBlock ( BlockPos pos , EnumHand hand , boolean swing , boolean exactHand ) {
        RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks ( new Vec3d ( BlockUtil.mc.player.posX , BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight ( ) , BlockUtil.mc.player.posZ ) , new Vec3d ( (double) pos.getX ( ) + 0.5 , (double) pos.getY ( ) - 0.5 , (double) pos.getZ ( ) + 0.5 ) );
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        BlockUtil.mc.player.connection.sendPacket ( (Packet) new CPacketPlayerTryUseItemOnBlock ( pos , facing , hand , 0.0f , 0.0f , 0.0f ) );
        if ( swing ) {
            BlockUtil.mc.player.connection.sendPacket ( (Packet) new CPacketAnimation ( exactHand ? hand : EnumHand.MAIN_HAND ) );
        }
    }

    public static
    BlockPos[] toBlockPos ( Vec3d[] vec3ds ) {
        BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; i++) {
            list[i] = new BlockPos ( vec3ds[i] );
        }
        return list;
    }

    public static
    Vec3d posToVec3d ( BlockPos pos ) {
        return new Vec3d ( pos );
    }

    public static
    BlockPos vec3dToPos ( Vec3d vec3d ) {
        return new BlockPos ( vec3d );
    }

    public static
    Boolean isPosInFov ( BlockPos pos ) {
        int dirnumber = RotationUtil.getDirection4D ( );

        if ( dirnumber == 0 && pos.getZ ( ) - mc.player.getPositionVector ( ).z < 0 ) {
            return false;
        }

        if ( dirnumber == 1 && pos.getX ( ) - mc.player.getPositionVector ( ).x > 0 ) {
            return false;
        }

        if ( dirnumber == 2 && pos.getZ ( ) - mc.player.getPositionVector ( ).z > 0 ) {
            return false;
        }

        return ! ( dirnumber == 3 && pos.getX ( ) - mc.player.getPositionVector ( ).x < 0 );
    }

    public static
    boolean isBlockBelowEntitySolid ( Entity entity ) {
        if ( entity != null ) {
            final BlockPos pos = new BlockPos ( entity.posX , entity.posY - 1.0 , entity.posZ );
            return isBlockSolid ( pos );
        }
        return false;
    }

    public static
    boolean isBlockSolid ( BlockPos pos ) {
        return ! isBlockUnSolid ( pos );
    }

    public static
    boolean isBlockUnSolid ( BlockPos pos ) {
        return isBlockUnSolid ( mc.world.getBlockState ( pos ).getBlock ( ) );
    }

    public static
    boolean isBlockUnSolid ( Block block ) {
        return unSolidBlocks.contains ( block );
    }

    public static
    Vec3d[] convertVec3ds ( Vec3d vec3d , Vec3d[] input ) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = vec3d.add ( input[i] );
        }
        return output;
    }

    public static
    Vec3d[] convertVec3ds ( EntityPlayer entity , Vec3d[] input ) {
        return convertVec3ds ( entity.getPositionVector ( ) , input );
    }

    public static
    boolean canBreak ( BlockPos pos ) {
        final IBlockState blockState = mc.world.getBlockState ( pos );
        final Block block = blockState.getBlock ( );
        return block.getBlockHardness ( blockState , mc.world , pos ) != - 1;
    }

    @SuppressWarnings("deprecation")
    public static
    boolean isValidBlock ( BlockPos pos ) {
        Block block = mc.world.getBlockState ( pos ).getBlock ( );
        return ! ( block instanceof BlockLiquid ) && block.getMaterial ( null ) != Material.AIR;
    }

    public static
    boolean isScaffoldPos ( BlockPos pos ) {
        return mc.world.isAirBlock ( pos )
                || mc.world.getBlockState ( pos ).getBlock ( ) == Blocks.SNOW_LAYER
                || mc.world.getBlockState ( pos ).getBlock ( ) == Blocks.TALLGRASS
                || mc.world.getBlockState ( pos ).getBlock ( ) instanceof BlockLiquid;
    }

    public static
    boolean rayTracePlaceCheck ( BlockPos pos , boolean shouldCheck , float height ) {
        return ! shouldCheck || mc.world.rayTraceBlocks ( new Vec3d ( mc.player.posX , mc.player.posY + (double) mc.player.getEyeHeight ( ) , mc.player.posZ ) , new Vec3d ( pos.getX ( ) , pos.getY ( ) + height , pos.getZ ( ) ) , false , true , false ) == null;
    }

    public static
    boolean rayTracePlaceCheck ( BlockPos pos , boolean shouldCheck ) {
        return rayTracePlaceCheck ( pos , shouldCheck , 1.0f );
    }

    public static
    boolean rayTracePlaceCheck ( BlockPos pos ) {
        return rayTracePlaceCheck ( pos , true );
    }

}
