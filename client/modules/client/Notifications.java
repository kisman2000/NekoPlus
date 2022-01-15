package me.vaimok.nekoplus.client.modules.client;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.manager.FileManager;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public
class Notifications extends Module {

    private static final String fileName = "client/util/ModuleMessage_List.txt";
    private static final List < String > modules = new ArrayList ( );
    private static Notifications INSTANCE = new Notifications ( );
    private final Timer timer = new Timer ( );
    public Setting < Boolean > totemPops = register ( new Setting ( "TotemPops" , false ) );
    public Setting < Boolean > totemNoti = register ( new Setting ( "TotemNoti" , true , v -> totemPops.getValue ( ) ) );
    public Setting < Integer > delay = register ( new Setting ( "Delay" , 2000 , 0 , 5000 , v -> totemPops.getValue ( ) , "Delays messages." ) );
    public Setting < Boolean > clearOnLogout = register ( new Setting ( "LogoutClear" , false ) );
    public Setting < Boolean > visualRange = register ( new Setting ( "VisualRange" , false ) );
    public Setting < Boolean > coords = register ( new Setting ( "Coords" , true , v -> visualRange.getValue ( ) ) );
    public Setting < Boolean > leaving = register ( new Setting ( "Leaving" , false , v -> visualRange.getValue ( ) ) );
    public Setting < Boolean > crash = register ( new Setting ( "Crash" , false ) );
    public Timer totemAnnounce = new Timer ( );
    private List < EntityPlayer > knownPlayers = new ArrayList <> ( );
    private boolean check;

    public
    Notifications ( ) {
        super ( "Notifications" , "Sends Messages." , Category.CLIENT , true , true , false );
        setInstance ( );
    }

    public static
    Notifications getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new Notifications ( );
        }
        return INSTANCE;
    }

    public static
    void displayCrash ( Exception e ) {
        Command.sendMessage ( TextUtil.RED + "Exception caught: " + e.getMessage ( ) );
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onLoad ( ) {
        check = true;
        loadFile ( );
        check = false;
    }

    @Override
    public
    void onEnable ( ) {
        this.knownPlayers = new ArrayList <> ( );
        if ( ! check ) {
            loadFile ( );
        }
    }

    @Override
    public
    void onUpdate ( ) {

        if ( visualRange.getValue ( ) ) {
            List < EntityPlayer > tickPlayerList = new ArrayList <> ( mc.world.playerEntities );
            if ( tickPlayerList.size ( ) > 0 ) {
                for (final EntityPlayer player : tickPlayerList) {
                    if ( player.getName ( ).equals ( mc.player.getName ( ) ) ) {
                        continue;
                    }
                    if ( ! knownPlayers.contains ( player ) ) {
                        knownPlayers.add ( player );
                        if ( nekoplus.friendManager.isFriend ( player ) ) {
                            Command.sendMessage ( "Player " + TextUtil.GREEN + player.getName ( ) + TextUtil.RESET + " entered your visual range" + ( coords.getValue ( ) ? " at (" + player.posX + ", " + player.posY + ", " + player.posZ + ")!" : "!" ) , true );
                        } else {
                            Command.sendMessage ( "Player " + TextUtil.RED + player.getName ( ) + TextUtil.RESET + " entered your visual range" + ( coords.getValue ( ) ? " at (" + player.posX + ", " + player.posY + ", " + player.posZ + ")!" : "!" ) , true );
                        }
                        return;
                    }
                }
            }

            if ( knownPlayers.size ( ) > 0 ) {
                for (EntityPlayer player : knownPlayers) {
                    if ( ! tickPlayerList.contains ( player ) ) {
                        knownPlayers.remove ( player );
                        if ( leaving.getValue ( ) ) {
                            if ( nekoplus.friendManager.isFriend ( player ) ) {
                                Command.sendMessage ( "Player " + TextUtil.GREEN + player.getName ( ) + TextUtil.RESET + " left your visual range" + ( coords.getValue ( ) ? " at (" + player.posX + ", " + player.posY + ", " + player.posZ + ")!" : "!" ) , true );
                            } else {
                                Command.sendMessage ( "Player " + TextUtil.RED + player.getName ( ) + TextUtil.RESET + " left your visual range" + ( coords.getValue ( ) ? " at (" + player.posX + ", " + player.posY + ", " + player.posZ + ")!" : "!" ) , true );
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    public
    void loadFile ( ) {
        List < String > fileInput = FileManager.readTextFileAllLines ( fileName );
        Iterator < String > i = fileInput.iterator ( );
        modules.clear ( );
        while ( i.hasNext ( ) ) {
            String s = i.next ( );
            if ( ! s.replaceAll ( "\\s" , "" ).isEmpty ( ) ) {
                modules.add ( s );
            }
        }
    }
}
