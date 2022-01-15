package me.vaimok.nekoplus.client.modules.movement;

import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;

public
class Step extends Module {
    public Setting < Integer > height = register ( new Setting ( "Height" , 2 , 0 , 5 ) );

    public
    Step ( ) {
        super ( "Step" , "Allows you to step up blocks" , Module.Category.MOVEMENT , true , false , false );

    }

    @Override
    public
    void onUpdate ( ) {
        if ( fullNullCheck ( ) ) return;
        mc.player.stepHeight = 2.0f;
    }

    @Override
    public
    void onDisable ( ) {
        super.onDisable ( );
        mc.player.stepHeight = 0.6f;
    }
}
