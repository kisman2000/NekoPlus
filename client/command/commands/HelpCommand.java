package me.vaimok.nekoplus.client.command.commands;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;

public
class HelpCommand extends Command {

    public
    HelpCommand ( ) {
        super ( "commands" );
    }

    @Override
    public
    void execute ( String[] commands ) {
        sendMessage ( "You can use following commands: " );
        for (Command command : nekoplus.commandManager.getCommands ( )) {
            sendMessage ( nekoplus.commandManager.getPrefix ( ) + command.getName ( ) );
        }
    }
}
