package me.vaimok.nekoplus.api.event.events;

import me.vaimok.nekoplus.api.event.EventStage;

public
class KeyEvent extends EventStage {

    public boolean info;
    public boolean pressed;

    public
    KeyEvent ( int stage , boolean info , boolean pressed ) {
        super ( stage );
        this.info = info;
        this.pressed = pressed;
    }
}
