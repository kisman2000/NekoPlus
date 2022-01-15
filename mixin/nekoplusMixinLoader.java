package me.vaimok.nekoplus.mixin;

import me.vaimok.nekoplus.nekoplus;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public
class nekoplusMixinLoader implements IFMLLoadingPlugin {

    private static boolean isObfuscatedEnvironment = false;

    public
    nekoplusMixinLoader ( ) {
        nekoplus.LOGGER.info ( "nekoplus mixins initialized" );
        MixinBootstrap.init ( );
        Mixins.addConfiguration ( "mixins.nekoplus.json" );
        MixinEnvironment.getDefaultEnvironment ( ).setObfuscationContext ( "searge" );
        nekoplus.LOGGER.info ( MixinEnvironment.getDefaultEnvironment ( ).getObfuscationContext ( ) );
    }

    @Override
    public
    String[] getASMTransformerClass ( ) {
        return new String[0];
    }

    @Override
    public
    String getModContainerClass ( ) {
        return null;
    }

    @Override
    public
    String getSetupClass ( ) {
        return null;
    }

    @Override
    public
    void injectData ( Map < String, Object > data ) {
        isObfuscatedEnvironment = (boolean) (Boolean) data.get ( "runtimeDeobfuscationEnabled" );
    }

    @Override
    public
    String getAccessTransformerClass ( ) {
        return null;
    }
}
