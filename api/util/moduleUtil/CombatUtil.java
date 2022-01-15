package me.vaimok.nekoplus.api.util.moduleUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public
class CombatUtil implements Minecraftable {
    public static
    EntityPlayer getTarget ( float range ) {
        EntityPlayer currentTarget = null;
        int size = CombatUtil.mc.world.playerEntities.size ( );
        for (int i = 0; i < size; ++ i) {
            EntityPlayer player = (EntityPlayer) CombatUtil.mc.world.playerEntities.get ( i );
            if ( EntityUtil.isntValid ( player , range ) ) continue;
            if ( currentTarget == null ) {
                currentTarget = player;
                continue;
            }
            if ( ! ( CombatUtil.mc.player.getDistanceSq ( player ) < CombatUtil.mc.player.getDistanceSq ( currentTarget ) ) )
                continue;
            currentTarget = player;
        }
        return currentTarget;
    }

    public static
    boolean isInHole ( EntityPlayer entity ) {
        return CombatUtil.isBlockValid ( new BlockPos ( entity.posX , entity.posY , entity.posZ ) );
    }

    public static
    boolean isBlockValid ( BlockPos blockPos ) {
        return CombatUtil.isBedrockHole ( blockPos ) || CombatUtil.isObbyHole ( blockPos ) || CombatUtil.isBothHole ( blockPos );
    }

    public static
    int isInHoleInt ( EntityPlayer entity ) {
        BlockPos playerPos = new BlockPos ( entity.getPositionVector ( ) );
        if ( CombatUtil.isBedrockHole ( playerPos ) ) {
            return 1;
        }
        if ( CombatUtil.isObbyHole ( playerPos ) || CombatUtil.isBothHole ( playerPos ) ) {
            return 2;
        }
        return 0;
    }

    public static
    boolean isObbyHole ( BlockPos blockPos ) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north ( ) , blockPos.south ( ) , blockPos.east ( ) , blockPos.west ( ) , blockPos.down ( )}) {
            IBlockState touchingState = CombatUtil.mc.world.getBlockState ( pos );
            if ( touchingState.getBlock ( ) == Blocks.OBSIDIAN ) continue;
            return false;
        }
        return true;
    }

    public static
    boolean isBedrockHole ( BlockPos blockPos ) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north ( ) , blockPos.south ( ) , blockPos.east ( ) , blockPos.west ( ) , blockPos.down ( )}) {
            IBlockState touchingState = CombatUtil.mc.world.getBlockState ( pos );
            if ( touchingState.getBlock ( ) == Blocks.BEDROCK ) continue;
            return false;
        }
        return true;
    }

    public static
    boolean isBothHole ( BlockPos blockPos ) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north ( ) , blockPos.south ( ) , blockPos.east ( ) , blockPos.west ( ) , blockPos.down ( )}) {
            IBlockState touchingState = CombatUtil.mc.world.getBlockState ( pos );
            if ( touchingState.getBlock ( ) == Blocks.BEDROCK || touchingState.getBlock ( ) == Blocks.OBSIDIAN )
                continue;
            return false;
        }
        return true;
    }
}
