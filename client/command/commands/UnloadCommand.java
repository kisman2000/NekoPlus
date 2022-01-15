package me.vaimok.nekoplus.client.command.commands;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;

public
class UnloadCommand extends Command {

    public
    UnloadCommand ( ) {
        super ( "unload" , new String[]{} );
    }

    @Override
    public
    void execute ( String[] commands ) {
        nekoplus.unload ( true );
    }
}
