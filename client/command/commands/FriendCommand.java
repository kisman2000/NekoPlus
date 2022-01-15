package me.vaimok.nekoplus.client.command.commands;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.api.manager.FriendManager;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;

public
class FriendCommand extends Command {

    public
    FriendCommand ( ) {
        super ( "friend" , new String[]{"<add/del/name/clear>" , "<name>"} );
    }

    @Override
    public
    void execute ( String[] commands ) {
        if ( commands.length == 1 ) {
            if ( nekoplus.friendManager.getFriends ( ).isEmpty ( ) ) {
                sendMessage ( "You currently dont have any friends added." );
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : nekoplus.friendManager.getFriends ( )) {
                    try {
                        f += friend.getUsername ( ) + ", ";
                    } catch ( Exception e ) {
                        continue;
                    }
                }
                sendMessage ( f );
            }
            return;
        }

        if ( commands.length == 2 ) {
            switch (commands[0]) {
                case "reset":
                    nekoplus.friendManager.onLoad ( );
                    sendMessage ( "Friends got reset." );
                    break;
                default:
                    sendMessage ( commands[0] + ( nekoplus.friendManager.isFriend ( commands[0] ) ? " is friended." : " isnt friended." ) );
                    break;
            }
            return;
        }

        if ( commands.length >= 2 ) {
            switch (commands[0]) {
                case "add":
                    nekoplus.friendManager.addFriend ( commands[1] );
                    sendMessage ( TextUtil.AQUA + commands[1] + " has been friended" );
                    break;
                case "del":
                    nekoplus.friendManager.removeFriend ( commands[1] );
                    sendMessage ( TextUtil.RED + commands[1] + " has been unfriended" );
                    break;
                default:
                    sendMessage ( TextUtil.RED + "Bad Command, try: friend <add/del/name> <name>." );
            }
        }
    }
}
