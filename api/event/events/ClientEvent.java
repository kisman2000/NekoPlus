package me.vaimok.nekoplus.api.event.events;

import me.vaimok.nekoplus.api.event.EventStage;
import me.vaimok.nekoplus.client.Client;
import me.vaimok.nekoplus.client.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public
class ClientEvent extends EventStage {

    private Client feature;
    private Setting setting;

    public
    ClientEvent ( int stage , Client feature ) {
        super ( stage );
        this.feature = feature;
    }

    public
    ClientEvent ( Setting setting ) {
        super ( 2 );
        this.setting = setting;
    }

    public Client getFeature ( ) {
        return this.feature;
    }

    public
    Setting getSetting ( ) {
        return this.setting;
    }
}
