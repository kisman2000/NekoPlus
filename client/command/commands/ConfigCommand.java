package me.vaimok.nekoplus.client.command.commands;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public
class ConfigCommand extends Command {

    public
    ConfigCommand ( ) {
        super ( "config" , new String[]{"<save/load>"} );
    }

    @Override
    public
    void execute ( String[] commands ) {
        if ( commands.length == 1 ) {
            sendMessage ( "You`ll find the config files in your gameProfile directory under client/config" );
            return;
        }

        if ( commands.length == 2 ) {
            if ( "list".equals ( commands[0] ) ) {
                String configs = "Configs: ";
                File file = new File ( "nekoplus/" );
                List < File > directories = Arrays.stream ( file.listFiles ( ) )
                        .filter ( File::isDirectory )
                        .filter ( f -> ! f.getName ( ).equals ( "util" ) )
                        .collect ( Collectors.toList ( ) );
                StringBuilder builder = new StringBuilder ( configs );
                for (File file1 : directories) {
                    builder.append ( file1.getName ( ) + ", " );
                }
                configs = builder.toString ( );
                sendMessage ( TextUtil.GREEN + configs );
            } else {
                sendMessage ( TextUtil.RED + "Not a valid command... Possible usage: <list>" );
            }
        }

        if ( commands.length >= 3 ) {
            switch (commands[0]) {
                case "save":
                    nekoplus.configManager.saveConfig ( commands[1] );
                    sendMessage ( TextUtil.GREEN + "Config has been saved." );
                    break;
                case "load":
                    nekoplus.configManager.loadConfig ( commands[1] );
                    sendMessage ( TextUtil.GREEN + "Config has been loaded." );
                    break;
                default:
                    sendMessage ( TextUtil.RED + "Not a valid command... Possible usage: <save/load>" );
                    break;
            }
        }
    }
}
