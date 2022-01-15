package me.vaimok.nekoplus.client.notifications;

import me.vaimok.nekoplus.client.modules.client.HUD;
import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public
class Notifications {
    private final String text;
    private final long disableTime;
    private final float width;
    private final Timer timer = new Timer ( );

    public
    Notifications ( String text , long disableTime ) {
        this.text = text;
        this.disableTime = disableTime;
        this.width = nekoplus.moduleManager.getModuleByClass ( HUD.class ).renderer.getStringWidth ( text );
        timer.reset ( );
    }

    public
    void onDraw ( int y ) {
        final ScaledResolution scaledResolution = new ScaledResolution ( Minecraft.getMinecraft ( ) );
        if ( timer.passedMs ( disableTime ) ) nekoplus.notificationManager.getNotifications ( ).remove ( this );
        RenderUtil.drawRect ( scaledResolution.getScaledWidth ( ) - 4 - width , y , scaledResolution.getScaledWidth ( ) - 2 , y + nekoplus.moduleManager.getModuleByClass ( HUD.class ).renderer.getFontHeight ( ) + 3 , 0x75000000 );
        nekoplus.moduleManager.getModuleByClass ( HUD.class ).renderer.drawString ( text , scaledResolution.getScaledWidth ( ) - width - 3 , y + 2 , - 1 , true );
    }
}
