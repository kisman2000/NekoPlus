package me.vaimok.nekoplus.client.modules.misc;

import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;

public
class QueueSkip extends Module {

    public
    QueueSkip ( ) {
        super ( "QueueSkip" , "skips 2b queue" , Category.MISC , true , false , false );
    }

    public
    void onEnable ( ) {
        Command.sendMessage ( TextUtil.LIGHT_PURPLE + "Skipping Queue..." );
        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Command.sendMessage ( TextUtil.LIGHT_PURPLE + "Queue skipped succesfully!" );
        toggle ( );
    }
}
