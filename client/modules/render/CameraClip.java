package me.vaimok.nekoplus.client.modules.render;

import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;

public
class CameraClip extends Module {

    private static CameraClip INSTANCE = new CameraClip ( );
    public Setting < Boolean > extend = register ( new Setting ( "Extend" , false ) );
    public Setting < Double > distance = register ( new Setting ( "Distance" , 10.0 , 0.0 , 50.0 , v -> extend.getValue ( ) , "By how much you want to extend the distance." ) );

    public
    CameraClip ( ) {
        super ( "CameraClip" , "Makes your Camera clip." , Category.RENDER , false , false , false );
        setInstance ( );
    }

    public static
    CameraClip getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new CameraClip ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }
}
