package me.vaimok.nekoplus.api.manager;


import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.modules.client.HUD;
import me.vaimok.nekoplus.client.notifications.Notifications;

import java.util.ArrayList;

public
class NotificationManager {
    private final ArrayList < Notifications > notifications = new ArrayList <> ( );

    public
    void handleNotifications ( int posY ) {
        for (int i = 0; i < getNotifications ( ).size ( ); i++) {
            getNotifications ( ).get ( i ).onDraw ( posY );
            posY -= nekoplus.moduleManager.getModuleByClass ( HUD.class ).renderer.getFontHeight ( ) + 5;
        }
    }

    public
    void addNotification ( String text , long duration ) {
        getNotifications ( ).add ( new Notifications ( text , duration ) );
    }

    public
    ArrayList < Notifications > getNotifications ( ) {
        return notifications;
    }
}
