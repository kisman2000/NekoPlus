package me.vaimok.nekoplus.api.event.events;

import me.vaimok.nekoplus.api.event.EventStage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public
class ProcessRightClickBlockEvent extends EventStage {

    public BlockPos pos;
    public EnumHand hand;
    public ItemStack stack;

    public
    ProcessRightClickBlockEvent ( BlockPos pos , EnumHand hand , ItemStack stack ) {
        super ( );
        this.pos = pos;
        this.hand = hand;
        this.stack = stack;
    }

}
