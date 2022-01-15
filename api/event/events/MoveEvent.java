package me.vaimok.nekoplus.api.event.events;

import me.vaimok.nekoplus.api.event.EventStage;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public
class MoveEvent extends EventStage {

    private MoverType type;
    private double x, y, z;

    public
    MoveEvent ( int stage , MoverType type , double x , double y , double z ) {
        super ( stage );
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public
    MoverType getType ( ) {
        return type;
    }

    public
    void setType ( MoverType type ) {
        this.type = type;
    }

    public
    double getX ( ) {
        return x;
    }

    public
    void setX ( double x ) {
        this.x = x;
    }

    public
    double getY ( ) {
        return y;
    }

    public
    void setY ( double y ) {
        this.y = y;
    }

    public
    double getZ ( ) {
        return z;
    }

    public
    void setZ ( double z ) {
        this.z = z;
    }
}
