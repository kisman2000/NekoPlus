package me.vaimok.nekoplus.mixin.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract
class MixinEntityPlayer extends EntityLivingBase {

    public
    MixinEntityPlayer ( World worldIn , GameProfile gameProfileIn ) {
        super ( worldIn );
    }

    @Inject(method = "getCooldownPeriod", at = @At("HEAD"), cancellable = true)
    private
    void getCooldownPeriodHook ( CallbackInfoReturnable < Float > callbackInfoReturnable ) {
        /*if (TpsSync.getInstance().isOn() && TpsSync.getInstance().attack.getValue()) {
            callbackInfoReturnable.setReturnValue((float)(1.0 / EntityPlayer.class.cast(this).getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0 * nekoplus.serverManager.getTpsFactor()));
        }*/
    }

    @ModifyConstant(method = "getPortalCooldown", constant = @Constant(intValue = 10))
    private
    int getPortalCooldownHook ( int cooldown ) {
        int time = cooldown;
        /*if(BetterPortals.getInstance().isOn() && BetterPortals.getInstance().fastPortal.getValue()) {
            time = BetterPortals.getInstance().cooldown.getValue();
        }*/
        return time;
    }

    @Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
    private
    void isEntityInsideOpaqueBlockHook ( CallbackInfoReturnable < Boolean > info ) {
    }

}
