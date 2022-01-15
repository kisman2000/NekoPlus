package me.vaimok.nekoplus.mixin.mixins;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.modules.client.Managers;
import me.vaimok.nekoplus.client.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.crash.CrashReport;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract
class MixinMinecraft {

    @Inject(method = "Lnet/minecraft/client/Minecraft;getLimitFramerate()I", at = @At("HEAD"), cancellable = true)
    public
    void getLimitFramerateHook ( CallbackInfoReturnable < Integer > callbackInfoReturnable ) {
        try {
            if ( Managers.getInstance ( ).unfocusedCpu.getValue ( ) && ! Display.isActive ( ) ) {
                callbackInfoReturnable.setReturnValue ( Managers.getInstance ( ).cpuFPS.getValue ( ) );
            }
        } catch ( NullPointerException ignored ) {
        }
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;sync(I)V"))
    public
    void syncHook ( int maxFps ) {
        if ( Managers.getInstance ( ).betterFrames.getValue ( ) ) {
            Display.sync ( Managers.getInstance ( ).betterFPS.getValue ( ) );
        } else {
            Display.sync ( maxFps );
        }
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public
    void displayCrashReportHook ( Minecraft minecraft , CrashReport crashReport ) {
        unload ( );
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;doVoidFogParticles(III)V"))
    public
    void doVoidFogParticlesHook ( WorldClient world , int x , int y , int z ) {
        NoRender.getInstance ( ).doVoidFogParticles ( x , y , z );
    }

    @Redirect(method = {"rightClickMouse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0))
    private
    boolean isHittingBlockHook ( PlayerControllerMP playerControllerMP ) {
        return playerControllerMP.getIsHittingBlock ( );
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public
    void shutdownHook ( CallbackInfo info ) {
        unload ( );
    }

    private
    void unload ( ) {
        System.out.println ( "Shutting down: saving configuration" );
        nekoplus.onUnload ( );
        System.out.println ( "Configuration saved." );
    }

}
