package me.vaimok.nekoplus.client.modules.misc;

import me.vaimok.nekoplus.api.util.discordUtil.DiscordUtil;
import me.vaimok.nekoplus.client.modules.Module;

public
class RPC extends Module {
    public static RPC INSTANCE;

    public
    RPC ( ) {
        super ( "RPC" , "DiscordUtil rich presence" , Category.CLIENT , false , false , false );
        INSTANCE = this;
    }

    @Override
    public
    void onEnable ( ) {
        super.onEnable ( );
        DiscordUtil.init();
    }

    @Override
    public
    void onDisable ( ) {
        super.onDisable ( );
        DiscordUtil.shutdown ( );
    }

}
