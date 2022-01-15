package me.vaimok.nekoplus.client.command.commands;

import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.nekoplus;

public
class ReloadCommand extends Command {

    public
    ReloadCommand ( ) {
        super ( "reload" , new String[]{} );
    }

    @Override
    public
    void execute ( String[] commands ) {
        nekoplus.reload ( );
    }
}
