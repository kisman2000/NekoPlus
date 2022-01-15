package me.vaimok.nekoplus.client.modules.movement;

import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;

public
class ReverseStep extends Module {
    //public Setting<eModes> mode = register(new Setting("Mode", Step.eModes.VANILLA));
    private final Setting < Integer > speed = this.register ( new Setting < Integer > ( "Speed" , 10 , 1 , 20 ) );

    public
    ReverseStep ( ) {
        super ( "ReverseStep" , "Go down" , Module.Category.MOVEMENT , true , false , false );
    }

    @Override
    public
    void onUpdate ( ) {
        if ( ReverseStep.fullNullCheck ( ) || this.mc.player.isInWater ( ) || this.mc.player.isInLava ( ) || this.mc.player.isOnLadder ( ) ) {
            return;
        }
        if ( this.mc.player.onGround ) {
            this.mc.player.motionY -= (float) this.speed.getValue ( ).intValue ( ) / 10.0f;
        }
    }

    @Override
    public
    void onDisable ( ) {
        super.onDisable ( );
        mc.player.motionY = 0;
    }
}
