package me.vaimok.nekoplus.mixin.mixins;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract
class MixinRenderLivingBase< T extends EntityLivingBase > extends Render < T > {

    public
    MixinRenderLivingBase ( RenderManager renderManagerIn , ModelBase modelBaseIn , float shadowSizeIn ) {
        super ( renderManagerIn );
    }

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    @Inject(method = "doRender", at = @At("HEAD"))
    public
    void doRenderPre ( T entity , double x , double y , double z , float entityYaw , float partialTicks , CallbackInfo info ) {
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    public
    void doRenderPost ( T entity , double x , double y , double z , float entityYaw , float partialTicks , CallbackInfo info ) {
    }
}
