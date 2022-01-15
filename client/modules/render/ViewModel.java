package me.vaimok.nekoplus.client.modules.render;

import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;

public
class ViewModel extends Module {

    private static ViewModel INSTANCE = new ViewModel ( );
    public Setting < Float > sizeX = register ( new Setting ( "SizeX" , 1f , 0f , 2f ) );
    public Setting < Float > sizeY = register ( new Setting ( "SizeY" , 1f , 0f , 2f ) );
    public Setting < Float > sizeZ = register ( new Setting ( "SizeZ" , 1f , 0f , 2f ) );
    public Setting < Float > rotationX = register ( new Setting ( "rotationX" , 0f , 0f , 1f ) );
    public Setting < Float > rotationY = register ( new Setting ( "rotationY" , 0f , 0f , 1f ) );
    public Setting < Float > rotationZ = register ( new Setting ( "rotationZ" , 0f , 0f , 1f ) );
    public Setting < Float > positionX = register ( new Setting ( "positionX" , 0f , - 2f , 2f ) );
    public Setting < Float > positionY = register ( new Setting ( "positionY" , 0f , - 2f , 2f ) );
    public Setting < Float > positionZ = register ( new Setting ( "positionZ" , 0f , - 2f , 2f ) );

    public
    ViewModel ( ) {
        super ( "Viewmodel" , "Changes to the viewmodel." , Category.RENDER , false , false , false );
        setInstance ( );
    }

    public static
    ViewModel getINSTANCE ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new ViewModel ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }
}