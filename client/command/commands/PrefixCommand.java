package me.vaimok.nekoplus.client.command.commands;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.client.ClickGui;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;

public
class PrefixCommand extends Command {

    public
    PrefixCommand ( ) {
        super ( "prefix" , new String[]{"<char>"} );
    }

    @Override
    public
    void execute ( String[] commands ) {
        if ( commands.length == 1 ) {
            Command.sendMessage ( TextUtil.RED + "Specify a new prefix." );
            return;
        }

        ( nekoplus.moduleManager.getModuleByClass ( ClickGui.class ) ).prefix.setValue ( commands[0] );
        Command.sendMessage ( "Prefix set to " + TextUtil.GREEN + nekoplus.commandManager.getPrefix ( ) );
    }
}
