package me.vaimok.nekoplus.client.modules.render;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.event.events.Render3DEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.modules.client.Colors;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.RotationUtil;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Random;

public
class HoleESP extends Module {
    private static HoleESP INSTANCE = new HoleESP ( );
    private final Setting < Integer > holes = this.register ( new Setting < Integer > ( "Holes" , 500 , 1 , 500 ) );
    private final Setting < Integer > red = this.register ( new Setting < Integer > ( "Red" , 255 , 0 , 255 ) );
    private final Setting < Integer > green = this.register ( new Setting < Integer > ( "Green" , 0 , 0 , 255 ) );
    private final Setting < Integer > blue = this.register ( new Setting < Integer > ( "Blue" , 0 , 0 , 255 ) );
    private final Setting < Integer > alpha = this.register ( new Setting < Integer > ( "Alpha" , 255 , 0 , 255 ) );
    public Setting < Boolean > ownHole = this.register ( new Setting < Boolean > ( "OwnHole" , true ) );
    public Setting < Boolean > box = this.register ( new Setting < Boolean > ( "Box" , true ) );
    private final Setting < Integer > boxAlpha = this.register ( new Setting < Object > ( "BoxAlpha" , Integer.valueOf ( 125 ) , Integer.valueOf ( 0 ) , Integer.valueOf ( 255 ) , v -> this.box.getValue ( ) ) );
    public Setting < Boolean > gradientBox = this.register ( new Setting < Object > ( "GradientBox" , Boolean.valueOf ( true ) , v -> this.box.getValue ( ) ) );
    public Setting < Boolean > pulseAlpha = this.register ( new Setting < Object > ( "PulseAlpha" , Boolean.valueOf ( false ) , v -> this.gradientBox.getValue ( ) ) );
    private final Setting < Integer > minPulseAlpha = this.register ( new Setting < Object > ( "MinPulse" , Integer.valueOf ( 10 ) , Integer.valueOf ( 0 ) , Integer.valueOf ( 255 ) , v -> this.pulseAlpha.getValue ( ) ) );
    private final Setting < Integer > maxPulseAlpha = this.register ( new Setting < Object > ( "MaxPulse" , Integer.valueOf ( 40 ) , Integer.valueOf ( 0 ) , Integer.valueOf ( 255 ) , v -> this.pulseAlpha.getValue ( ) ) );
    private final Setting < Integer > pulseSpeed = this.register ( new Setting < Object > ( "PulseSpeed" , Integer.valueOf ( 10 ) , Integer.valueOf ( 1 ) , Integer.valueOf ( 50 ) , v -> this.pulseAlpha.getValue ( ) ) );
    public Setting < Boolean > invertGradientBox = this.register ( new Setting < Object > ( "InvertGradientBox" , Boolean.valueOf ( false ) , v -> this.gradientBox.getValue ( ) ) );
    public Setting < Boolean > outline = this.register ( new Setting < Boolean > ( "Outline" , true ) );
    private final Setting < Float > lineWidth = this.register ( new Setting < Object > ( "LineWidth" , Float.valueOf ( 1.0f ) , Float.valueOf ( 0.1f ) , Float.valueOf ( 5.0f ) , v -> this.outline.getValue ( ) ) );
    public Setting < Boolean > gradientOutline = this.register ( new Setting < Object > ( "GradientOutline" , Boolean.valueOf ( true ) , v -> this.outline.getValue ( ) ) );
    public Setting < Boolean > invertGradientOutline = this.register ( new Setting < Object > ( "InvertGradientOutline" , Boolean.valueOf ( true ) , v -> this.gradientOutline.getValue ( ) ) );
    public Setting < Double > height = this.register ( new Setting < Double > ( "Height" , 0.0 , - 2.0 , 2.0 ) );
    public Setting < Boolean > sync = register ( new Setting ( "ColorSync" , false ) );
    public Setting < Sync > syncMode = register ( new Setting ( "SyncMode" , Sync.BOTH , v -> sync.getValue ( ) ) );
    public Setting < Boolean > safeColor = this.register ( new Setting < Boolean > ( "SafeColor" , true ) );
    private final Setting < Integer > safeRed = this.register ( new Setting < Object > ( "SafeRed" , Integer.valueOf ( 0 ) , Integer.valueOf ( 0 ) , Integer.valueOf ( 255 ) , v -> this.safeColor.getValue ( ) ) );
    private final Setting < Integer > safeGreen = this.register ( new Setting < Object > ( "SafeGreen" , Integer.valueOf ( 255 ) , Integer.valueOf ( 0 ) , Integer.valueOf ( 255 ) , v -> this.safeColor.getValue ( ) ) );
    private final Setting < Integer > safeBlue = this.register ( new Setting < Object > ( "SafeBlue" , Integer.valueOf ( 0 ) , Integer.valueOf ( 0 ) , Integer.valueOf ( 255 ) , v -> this.safeColor.getValue ( ) ) );
    private final Setting < Integer > safeAlpha = this.register ( new Setting < Object > ( "SafeAlpha" , Integer.valueOf ( 255 ) , Integer.valueOf ( 0 ) , Integer.valueOf ( 255 ) , v -> this.safeColor.getValue ( ) ) );
    Color safecolor = new Color ( safeRed.getValue ( ) , safeGreen.getValue ( ) , safeBlue.getValue ( ) , safeAlpha.getValue ( ) );
    Color obbycolor = new Color ( red.getValue ( ) , green.getValue ( ) , blue.getValue ( ) , alpha.getValue ( ) );
    private boolean pulsing = false;
    private boolean shouldDecrease = false;
    private int pulseDelay = 0;
    private int currentPulseAlpha;
    private int currentAlpha = 0;

    public
    HoleESP ( ) {
        super ( "HoleESP" , "Shows safe spots." , Category.RENDER , false , false , false );
        setInstance ( );
    }

    public static
    HoleESP getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new HoleESP ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onRender3D ( Render3DEvent event ) {
        int drawnHoles = 0;
        if ( ! this.pulsing && this.pulseAlpha.getValue ( ).booleanValue ( ) ) {
            Random rand = new Random ( );
            this.currentPulseAlpha = rand.nextInt ( this.maxPulseAlpha.getValue ( ) - this.minPulseAlpha.getValue ( ) + 1 ) + this.minPulseAlpha.getValue ( );
            this.pulsing = true;
            this.shouldDecrease = false;
        }
        if ( this.pulseDelay == 0 ) {
            if ( this.pulsing && this.pulseAlpha.getValue ( ).booleanValue ( ) && ! this.shouldDecrease ) {
                ++ this.currentAlpha;
                if ( this.currentAlpha >= this.currentPulseAlpha ) {
                    this.shouldDecrease = true;
                }
            }
            if ( this.pulsing && this.pulseAlpha.getValue ( ).booleanValue ( ) && this.shouldDecrease ) {
                -- this.currentAlpha;
            }
            if ( this.currentAlpha <= 0 ) {
                this.pulsing = false;
                this.shouldDecrease = false;
            }
            ++ this.pulseDelay;
        } else {
            ++ this.pulseDelay;
            if ( this.pulseDelay == 51 - this.pulseSpeed.getValue ( ) ) {
                this.pulseDelay = 0;
            }
        }
        if ( ! this.pulseAlpha.getValue ( ).booleanValue ( ) || ! this.pulsing ) {
            this.currentAlpha = 0;
        }
        for (BlockPos pos : nekoplus.holeManager.getSortedHoles ( )) {
            if ( drawnHoles >= this.holes.getValue ( ) ) break;
            if ( pos.equals ( new BlockPos ( HoleESP.mc.player.posX , HoleESP.mc.player.posY , HoleESP.mc.player.posZ ) ) && ! this.ownHole.getValue ( ).booleanValue ( ) || ! RotationUtil.isInFov ( pos ) )
                continue;
            if ( this.safeColor.getValue ( ).booleanValue ( ) && nekoplus.holeManager.isSafe ( pos ) ) {
                RenderUtil.drawBoxESP ( pos , ( ( syncMode.getValue ( ) == Sync.SAFE || syncMode.getValue ( ) == Sync.BOTH ) && sync.getValue ( ) ) ? Colors.INSTANCE.getCurrentColor ( ) : safecolor , false , ( ( syncMode.getValue ( ) == Sync.SAFE || syncMode.getValue ( ) == Sync.BOTH ) && sync.getValue ( ) ) ? Colors.INSTANCE.getCurrentColor ( ) : safecolor , this.lineWidth.getValue ( ).floatValue ( ) , this.outline.getValue ( ) , this.box.getValue ( ) , this.boxAlpha.getValue ( ) , true , this.height.getValue ( ) , this.gradientBox.getValue ( ) , this.gradientOutline.getValue ( ) , this.invertGradientBox.getValue ( ) , this.invertGradientOutline.getValue ( ) , this.currentAlpha );
            } else {
                RenderUtil.drawBoxESP ( pos , ( ( syncMode.getValue ( ) == Sync.OBBY || syncMode.getValue ( ) == Sync.BOTH ) && sync.getValue ( ) ) ? Colors.INSTANCE.getCurrentColor ( ) : obbycolor , false , ( ( syncMode.getValue ( ) == Sync.OBBY || syncMode.getValue ( ) == Sync.BOTH ) && sync.getValue ( ) ) ? Colors.INSTANCE.getCurrentColor ( ) : obbycolor , this.lineWidth.getValue ( ).floatValue ( ) , this.outline.getValue ( ) , this.box.getValue ( ) , this.boxAlpha.getValue ( ) , true , this.height.getValue ( ) , this.gradientBox.getValue ( ) , this.gradientOutline.getValue ( ) , this.invertGradientBox.getValue ( ) , this.invertGradientOutline.getValue ( ) , this.currentAlpha );
            }
            ++ drawnHoles;
        }
    }

    private
    enum Sync {
        OBBY,
        SAFE,
        BOTH
    }
}
