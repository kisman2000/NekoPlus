package me.vaimok.nekoplus.client.modules.player;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.event.events.BlockEvent;
import me.vaimok.nekoplus.api.event.events.PacketEvent;
import me.vaimok.nekoplus.api.event.events.Render3DEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.modules.client.Colors;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.BlockUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

import static net.minecraft.network.play.client.CPacketPlayerDigging.Action.START_DESTROY_BLOCK;
import static net.minecraft.network.play.client.CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK;

public
class FastMine extends Module {

    private static FastMine INSTANCE = new FastMine( );
    private final Timer timer = new Timer ( );
    public Setting < Boolean > tweaks = register ( new Setting ( "Tweaks" , true ) );
    public Setting < Boolean > reset = register ( new Setting ( "Reset" , true ) );
    public Setting < Boolean > noBreakAnim = register ( new Setting ( "NoBreakAnim" , false ) );
    public Setting < Boolean > noDelay = register ( new Setting ( "NoDelay" , false ) );
    public Setting < Boolean > noSwing = register ( new Setting ( "NoSwing" , false ) );
    public Setting < Boolean > noTrace = register ( new Setting ( "NoTrace" , false ) );
    public Setting < Boolean > allow = register ( new Setting ( "AllowMultiTask" , false ) );
    public Setting < Boolean > pickaxe = register ( new Setting ( "Pickaxe" , true , v -> noTrace.getValue ( ) ) );
    public Setting < Boolean > doubleBreak = register ( new Setting ( "DoubleBreak" , false ) );
    public Setting < Boolean > render = register ( new Setting ( "Render" , false ) );
    public Setting < Boolean > box = register ( new Setting ( "Box" , false , v -> render.getValue ( ) ) );
    private final Setting < Integer > boxAlpha = register ( new Setting ( "BoxAlpha" , 85 , 0 , 255 , v -> box.getValue ( ) && render.getValue ( ) ) );
    public Setting < Boolean > outline = register ( new Setting ( "Outline" , true , v -> render.getValue ( ) ) );
    private final Setting < Float > lineWidth = register ( new Setting ( "LineWidth" , 1.0f , 0.1f , 5.0f , v -> outline.getValue ( ) && render.getValue ( ) ) );
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    private boolean isMining = false;
    private BlockPos lastPos = null;
    private EnumFacing lastFacing = null;

    public FastMine( ) {
        super ( "FastMine" , "Speeds up mining." , Category.PLAYER , true , false , false );
        setInstance ( );
    }

    public static FastMine getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new FastMine( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onTick ( ) {
        if ( currentPos != null ) {
            if ( ! mc.world.getBlockState ( currentPos ).equals ( currentBlockState ) || mc.world.getBlockState ( currentPos ).getBlock ( ) == Blocks.AIR ) {
                currentPos = null;
                currentBlockState = null;
            }
        }
    }

    @Override
    public
    void onUpdate ( ) {
        if ( fullNullCheck ( ) ) {
            return;
        }

        if ( noDelay.getValue ( ) ) {
            mc.playerController.blockHitDelay = 0;
        }

        if ( isMining && lastPos != null && lastFacing != null && noBreakAnim.getValue ( ) ) {
            mc.player.connection.sendPacket ( new CPacketPlayerDigging ( CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK , lastPos , lastFacing ) );
        }

        if ( reset.getValue ( ) && mc.gameSettings.keyBindUseItem.isKeyDown ( ) && ! allow.getValue ( ) ) {
            mc.playerController.isHittingBlock = false;
        }
    }

    @Override
    public
    void onRender3D ( Render3DEvent event ) {
        if ( render.getValue ( ) && currentPos != null ) {
            Color color = new Color ( 255 , 255 , 255 , 255 );
            Color readyColor = Colors.INSTANCE.isEnabled ( ) ? Colors.INSTANCE.getCurrentColor ( ) : new Color ( 125 , 105 , 255 , 255 );
            RenderUtil.drawBoxESP ( currentPos , timer.passedMs ( (int) ( 2000 * nekoplus.serverManager.getTpsFactor ( ) ) ) ? readyColor : color , false , color , lineWidth.getValue ( ) , outline.getValue ( ) , box.getValue ( ) , boxAlpha.getValue ( ) , false );
        }
    }

    @SubscribeEvent
    public
    void onPacketSend ( PacketEvent.Send event ) {
        if ( fullNullCheck ( ) ) {
            return;
        }

        if ( event.getStage ( ) == 0 ) {
            if ( noSwing.getValue ( ) && event.getPacket ( ) instanceof CPacketAnimation ) {
                event.setCanceled ( true );
            }

            if ( noBreakAnim.getValue ( ) && event.getPacket ( ) instanceof CPacketPlayerDigging ) {
                CPacketPlayerDigging packet = event.getPacket ( );
                if ( packet != null && packet.getPosition ( ) != null ) {
                    try {
                        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity ( null , new AxisAlignedBB ( packet.getPosition ( ) ) )) {
                            if ( entity instanceof EntityEnderCrystal ) {
                                showAnimation ( );
                                return;
                            }
                        }
                    } catch ( Exception ignored ) {
                    }

                    if ( packet.getAction ( ).equals ( START_DESTROY_BLOCK ) ) {
                        showAnimation ( true , packet.getPosition ( ) , packet.getFacing ( ) );
                    }

                    if ( packet.getAction ( ).equals ( STOP_DESTROY_BLOCK ) ) {
                        showAnimation ( );
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public
    void onBlockEvent ( BlockEvent event ) {
        if ( fullNullCheck ( ) ) {
            return;
        }

        if ( event.getStage ( ) == 3 && reset.getValue ( ) ) {
            if ( mc.playerController.curBlockDamageMP > 0.1f ) {
                mc.playerController.isHittingBlock = true;
            }
        }

        if ( event.getStage ( ) == 4 && tweaks.getValue ( ) ) {
            if ( BlockUtil.canBreak ( event.pos ) ) {
                if ( reset.getValue ( ) ) {
                    mc.playerController.isHittingBlock = false;
                }

                if ( currentPos == null ) {
                    currentPos = event.pos;
                    currentBlockState = mc.world.getBlockState ( currentPos );
                    timer.reset ( );
                }
                mc.player.swingArm ( EnumHand.MAIN_HAND );
                mc.player.connection.sendPacket ( new CPacketPlayerDigging ( START_DESTROY_BLOCK , event.pos , event.facing ) );
                mc.player.connection.sendPacket ( new CPacketPlayerDigging ( STOP_DESTROY_BLOCK , event.pos , event.facing ) );
                event.setCanceled ( true );
            }

            if ( doubleBreak.getValue ( ) ) {
                BlockPos above = event.pos.add ( 0 , 1 , 0 );
                if ( BlockUtil.canBreak ( above ) && mc.player.getDistance ( above.getX ( ) , above.getY ( ) , above.getZ ( ) ) <= 5f ) {
                    mc.player.swingArm ( EnumHand.MAIN_HAND );
                    mc.player.connection.sendPacket ( new CPacketPlayerDigging ( START_DESTROY_BLOCK , above , event.facing ) );
                    mc.player.connection.sendPacket ( new CPacketPlayerDigging ( STOP_DESTROY_BLOCK , above , event.facing ) );
                    mc.playerController.onPlayerDestroyBlock ( above );
                    mc.world.setBlockToAir ( above );
                }
            }
        }
    }

    private
    void showAnimation ( boolean isMining , BlockPos lastPos , EnumFacing lastFacing ) {
        this.isMining = isMining;
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public
    void showAnimation ( ) {
        showAnimation ( false , null , null );
    }

    @Override
    public
    String getDisplayInfo ( ) {
        return "Packet";
    }
}