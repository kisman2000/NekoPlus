package me.vaimok.nekoplus.api.manager;

import me.vaimok.nekoplus.client.Client;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public
class FriendManager extends Client {

    private List < Friend > friends = new ArrayList <> ( );

    public
    FriendManager ( ) {
        super ( "Friends" );
    }

    public
    boolean isFriend ( String name ) {
        cleanFriends ( );
        return this.friends.stream ( ).anyMatch ( friend -> friend.username.equalsIgnoreCase ( name ) );
    }

    public
    boolean isFriend ( EntityPlayer player ) {
        return this.isFriend ( player.getName ( ) );
    }

    public
    void addFriend ( String name ) {
        Friend friend = getFriendByName ( name );
        if ( friend != null ) {
            this.friends.add ( friend );
        }
        cleanFriends ( );
    }

    public
    void removeFriend ( String name ) {
        cleanFriends ( );
        for (Friend friend : this.friends) {
            if ( friend.getUsername ( ).equalsIgnoreCase ( name ) ) {
                friends.remove ( friend );
                break;
            }
        }
    }

    public
    void onLoad ( ) {
        this.friends = new ArrayList <> ( );
        this.clearSettings ( );
    }

    public
    void saveFriends ( ) {
        this.clearSettings ( );
        cleanFriends ( );
        for (Friend friend : this.friends) {
            register ( new Setting ( friend.getUuid ( ).toString ( ) , friend.getUsername ( ) ) );
        }
    }

    public
    void cleanFriends ( ) {
        this.friends.stream ( ).filter ( Objects::nonNull ).filter ( friend -> friend.getUsername ( ) != null );
    }

    public
    List < Friend > getFriends ( ) {
        cleanFriends ( );
        return this.friends;
    }

    public
    Friend getFriendByName ( String input ) {
        UUID uuid = PlayerUtil.getUUIDFromName ( input );
        if ( uuid != null ) {
            Friend friend = new Friend ( input , uuid );
            return friend;
        }
        return null;
    }

    public
    void addFriend ( Friend friend ) {
        this.friends.add ( friend );
    }

    public static
    class Friend {

        private final String username;
        private final UUID uuid;

        public
        Friend ( String username , UUID uuid ) {
            this.username = username;
            this.uuid = uuid;
        }

        public
        String getUsername ( ) {
            return username;
        }

        public
        UUID getUuid ( ) {
            return uuid;
        }
    }
}
