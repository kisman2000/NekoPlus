package me.vaimok.nekoplus.client.modules.client;

import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.SoundEvents;

import java.util.HashSet;
import java.util.Set;

public
class GhastFinder
        extends Module {
    private final Set < Entity > ghasts = new HashSet < Entity > ( );
    public Setting < Boolean > Chat = this.register ( new Setting < Boolean > ( "Chat" , true ) );
    public Setting < Boolean > Sound = this.register ( new Setting < Boolean > ( "Sound" , true ) );

    public GhastFinder( ) {
        super ( "GhastFinder" , "for wakespace" , Category.CLIENT , true , false , false );
    }

    @Override
    public
    void onEnable ( ) {
        this.ghasts.clear ( );
    }

    @Override
    public
    void onUpdate ( ) {
        for (Entity entity : GhastFinder.mc.world.getLoadedEntityList ( )) {
            if ( ! ( entity instanceof EntityGhast ) || this.ghasts.contains ( entity ) ) continue;
            if ( this.Chat.getValue ( ) ) {
                Command.sendMessage (TextUtil.GREEN + "Yo! I found a ghast near: " + entity.getPosition ( ).getX ( ) + "x, " + entity.getPosition ( ).getY ( ) + "y, " + entity.getPosition ( ).getZ ( ) + "z." );
            }
            this.ghasts.add ( entity );
            if ( ! this.Sound.getValue ( ) ) continue;
            GhastFinder.mc.player.playSound ( SoundEvents.BLOCK_ANVIL_DESTROY , 1.0f , 1.0f );
        }
    }
}

