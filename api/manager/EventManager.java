package me.vaimok.nekoplus.api.manager;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.event.events.*;
import me.vaimok.nekoplus.client.Client;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.client.Managers;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import me.vaimok.nekoplus.api.util.moduleUtil.Util;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.atomic.AtomicBoolean;

public
class EventManager extends Client {

    private final Timer timer = new Timer ( );
    private final Timer logoutTimer = new Timer ( );
    private final AtomicBoolean tickOngoing = new AtomicBoolean ( false );

    public
    void init ( ) {
        MinecraftForge.EVENT_BUS.register ( this );
    }

    public
    void onUnload ( ) {
        MinecraftForge.EVENT_BUS.unregister ( this );
    }

    @SubscribeEvent
    public
    void onUpdate ( LivingEvent.LivingUpdateEvent event ) {
        if ( ! fullNullCheck ( ) && event.getEntity ( ).getEntityWorld ( ).isRemote && event.getEntityLiving ( ).equals ( Util.mc.player ) ) {
            nekoplus.potionManager.update ( );
            nekoplus.totemPopManager.onUpdate ( );
            nekoplus.inventoryManager.update ( );
            nekoplus.holeManager.update ( );
            nekoplus.moduleManager.onUpdate ( );
            nekoplus.timerManager.update ( );
            if ( timer.passedMs ( Managers.getInstance ( ).moduleListUpdates ) ) {
                nekoplus.moduleManager.sortModules ( true );
                timer.reset ( );
            }
        }
    }

    @SubscribeEvent
    public
    void onClientConnect ( FMLNetworkEvent.ClientConnectedToServerEvent event ) {
        logoutTimer.reset ( );
        nekoplus.moduleManager.onLogin ( );
    }

    @SubscribeEvent
    public
    void onClientDisconnect ( FMLNetworkEvent.ClientDisconnectionFromServerEvent event ) {
        nekoplus.moduleManager.onLogout ( );
        nekoplus.totemPopManager.onLogout ( );
        nekoplus.potionManager.onLogout ( );
    }

    public
    boolean ticksOngoing ( ) {
        return this.tickOngoing.get ( );
    }

    @SubscribeEvent
    public
    void onTick ( TickEvent.ClientTickEvent event ) {
        if ( fullNullCheck ( ) ) {
            return;
        }

        nekoplus.moduleManager.onTick ( );

        for (EntityPlayer player : Util.mc.world.playerEntities) {

            if ( player == null || player.getHealth ( ) > 0 ) {
                continue;
            }

            MinecraftForge.EVENT_BUS.post ( new DeathEvent ( player ) );
            nekoplus.totemPopManager.onDeath ( player );
        }
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( UpdateWalkingPlayerEvent event ) {
        if ( fullNullCheck ( ) ) return;

        if ( event.getStage ( ) == 0 ) {
            nekoplus.speedManager.updateValues ( );
            nekoplus.rotationManager.updateRotations ( );
            nekoplus.positionManager.updatePosition ( );
        }

        if ( event.getStage ( ) == 1 ) {
            nekoplus.rotationManager.restoreRotations ( );
            nekoplus.positionManager.restorePosition ( );
        }
    }

    @SubscribeEvent
    public
    void onPacketReceive ( PacketEvent.Receive event ) {
        if ( event.getStage ( ) != 0 ) {
            return;
        }

        nekoplus.serverManager.onPacketReceived ( );

        if ( event.getPacket ( ) instanceof SPacketEntityStatus ) {
            SPacketEntityStatus packet = event.getPacket ( );
            if ( packet.getOpCode ( ) == 35 ) {
                if ( packet.getEntity ( Util.mc.world ) instanceof EntityPlayer ) {
                    EntityPlayer player = (EntityPlayer) packet.getEntity ( Util.mc.world );
                    MinecraftForge.EVENT_BUS.post ( new TotemPopEvent ( player ) );
                    nekoplus.totemPopManager.onTotemPop ( player );
                    nekoplus.potionManager.onTotemPop ( player );
                }
            }
        }

        if ( event.getPacket ( ) instanceof SPacketPlayerListItem && ! fullNullCheck ( ) && logoutTimer.passedS ( 1 ) ) {
            final SPacketPlayerListItem packet = event.getPacket ( );
            if ( ! SPacketPlayerListItem.Action.ADD_PLAYER.equals ( packet.getAction ( ) ) && ! SPacketPlayerListItem.Action.REMOVE_PLAYER.equals ( packet.getAction ( ) ) ) {
                return;
            }
        }

        if ( event.getPacket ( ) instanceof SPacketTimeUpdate ) {
            nekoplus.serverManager.update ( );
        }
    }

    @SubscribeEvent
    public
    void onWorldRender ( RenderWorldLastEvent event ) {
        if ( event.isCanceled ( ) ) {
            return;
        }

        Util.mc.profiler.startSection ( "client" );
        GlStateManager.disableTexture2D ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableAlpha ( );
        GlStateManager.tryBlendFuncSeparate ( GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA , 1 , 0 );
        GlStateManager.shadeModel ( GL11.GL_SMOOTH );
        GlStateManager.disableDepth ( );
        GlStateManager.glLineWidth ( 1f );
        Render3DEvent render3dEvent = new Render3DEvent ( event.getPartialTicks ( ) );
        nekoplus.moduleManager.onRender3D ( render3dEvent );
        //MinecraftForge.EVENT_BUS.post(render3dEvent);
        GlStateManager.glLineWidth ( 1f );
        GlStateManager.shadeModel ( GL11.GL_FLAT );
        GlStateManager.disableBlend ( );
        GlStateManager.enableAlpha ( );
        GlStateManager.enableTexture2D ( );
        GlStateManager.enableDepth ( );
        GlStateManager.enableCull ( );
        GlStateManager.enableCull ( );
        GlStateManager.depthMask ( true );
        GlStateManager.enableTexture2D ( );
        GlStateManager.enableBlend ( );
        GlStateManager.enableDepth ( );
        Util.mc.profiler.endSection ( );
    }

    @SubscribeEvent
    public
    void renderHUD ( RenderGameOverlayEvent.Post event ) {
        if ( event.getType ( ) == RenderGameOverlayEvent.ElementType.HOTBAR ) {
            nekoplus.textManager.updateResolution ( );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public
    void onRenderGameOverlayEvent ( RenderGameOverlayEvent.Text event ) {
        if ( event.getType ( ).equals ( RenderGameOverlayEvent.ElementType.TEXT ) ) {
            final ScaledResolution resolution = new ScaledResolution ( Util.mc );
            Render2DEvent render2DEvent = new Render2DEvent ( event.getPartialTicks ( ) , resolution );
            nekoplus.moduleManager.onRender2D ( render2DEvent );
            GlStateManager.color ( 1.f , 1.f , 1.f , 1.f );
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public
    void onKeyInput ( InputEvent.KeyInputEvent event ) {
        if ( Keyboard.getEventKeyState ( ) ) {
            nekoplus.moduleManager.onKeyPressed ( Keyboard.getEventKey ( ) );
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public
    void onChatSent ( ClientChatEvent event ) {
        if ( event.getMessage ( ).startsWith ( Command.getCommandPrefix ( ) ) ) {
            event.setCanceled ( true );
            try {
                Util.mc.ingameGUI.getChatGUI ( ).addToSentMessages ( event.getMessage ( ) );
                if ( event.getMessage ( ).length ( ) > 1 ) {
                    nekoplus.commandManager.executeCommand ( event.getMessage ( ).substring ( Command.getCommandPrefix ( ).length ( ) - 1 ) );
                } else {
                    Command.sendMessage ( "Please enter a command." );
                }
            } catch ( Exception e ) {
                e.printStackTrace ( );
                Command.sendMessage ( TextUtil.RED + "An error occurred while running this command. Check the log!" );
            }
            event.setMessage ( "" );
        }
    }
}
