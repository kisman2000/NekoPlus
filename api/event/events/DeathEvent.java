package me.vaimok.nekoplus.api.event.events;

import me.vaimok.nekoplus.api.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public
class DeathEvent extends EventStage {

    public EntityPlayer player;

    public
    DeathEvent ( EntityPlayer player ) {
        super ( );
        this.player = player;
    }

}
