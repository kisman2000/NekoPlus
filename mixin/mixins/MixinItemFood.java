package me.vaimok.nekoplus.mixin.mixins;

import net.minecraft.item.ItemFood;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemFood.class)
public
class MixinItemFood {

    /*@Inject(method = "onItemUseFinish", at = @At("RETURN"), cancellable = true)
    public void onItemUseFinishHook(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> info) {
        Offhand.getInstance().onItemFinish(stack, entityLiving);
    }*/
}
