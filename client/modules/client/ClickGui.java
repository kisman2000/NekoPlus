package me.vaimok.nekoplus.client.modules.client;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.event.events.ClientEvent;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.gui.nekoplusGui;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", true));


    public Setting<String> prefix = this.register (new Setting("Prefix", "."));
    public Setting<Integer> red = this.register (new Setting("Red", 255, 0, 255));
    public Setting<Integer> green = this.register (new Setting("Green", 0, 0, 255));
    public Setting<Integer> blue = this.register (new Setting("Blue", 0, 0, 255));
    public Setting<Integer> hoverAlpha = this.register (new Setting("Alpha", 180, 0, 255));
    public Setting<Integer> alpha = this.register (new Setting("HoverAlpha", 240, 0, 255));
    public Setting<Boolean> customFov = this.register(new Setting("CustomFov", false));
    public Setting<Float> fov = this.register(new Setting("Fov", 150.0f, -180.0f, 180.0f, v -> customFov.getValue()));
    public Setting<String> moduleButton = this.register(new Setting("Buttons", ""));
    public Setting<Boolean> devSettings = this.register(new Setting("TopBarSettings", false));
    public Setting<Integer> topRed = this.register(new Setting("TopRed", 255, 0, 255, v -> devSettings.getValue()));
    public Setting<Integer> topGreen = this.register(new Setting("TopGreen", 0, 0, 255, v -> devSettings.getValue()));
    public Setting<Integer> topBlue = this.register(new Setting("TopBlue", 0, 0, 255, v -> devSettings.getValue()));
    public Setting<Integer> topAlpha = this.register(new Setting("TopAlpha", 255, 0, 255, v -> devSettings.getValue()));
    public Setting<Boolean> shader = this.register(new Setting("Blur", true));
    public Setting<Boolean> textShadow = this.register(new Setting("Font Shadow", true));;
    public Setting<Integer> sizeWidth = this.register(new Setting("Width", 20, 0, 50 ));
    public Setting<Float> olWidth = this.register(new Setting("Outline Width", 0.0f, 1f, 5f));
    public Setting<Integer> textSize = this.register(new Setting("Text Size", 21, "FontSize"));


    private static ClickGui INSTANCE = new ClickGui();

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Category.CLIENT, true, false, false);
        setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static ClickGui getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new nekoplusGui());
        if (customFov.getValue()) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.getValue());
        }
        if (shader.getValue() == true) {
            if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
                if (mc.entityRenderer.getShaderGroup() != null) {
                    mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }
                mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            }
        }
        if (shader.getValue() == false) {
            if (mc.entityRenderer.getShaderGroup() != null) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                mc.entityRenderer.stopUseShader();
            }
        }
    }


    public static ClickGui getGui() {
        return getInstance();
    }

    public float getOutlineWidth() {
        return olWidth.getValue();
    }

    public float getSizeWidth() {
        return sizeWidth.getValue();
    }



    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if(event.getStage() == 2) {
            if(event.getSetting().getFeature().equals(this)) {
                if(event.getSetting().equals(this.prefix)) {
                    nekoplus.commandManager.setPrefix(this.prefix.getPlannedValue());
                    Command.sendMessage("Prefix set to " + TextUtil.BLUE + nekoplus.commandManager.getPrefix());
                }
                nekoplus.colorManager.setColor(red.getPlannedValue(), green.getPlannedValue(), blue.getPlannedValue(), hoverAlpha.getPlannedValue());
            }
        }
    }

    @Override
    public void onLoad() {
        if (this.colorSync.getValue().booleanValue()) {
            nekoplus.colorManager.setColor(Colors.getInstance().getCurrentColor().getRed(), Colors.getInstance().getCurrentColor().getGreen(), Colors.getInstance().getCurrentColor().getBlue(), this.hoverAlpha.getValue());
        } else {
            nekoplus.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        }
        nekoplus.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if(!(mc.currentScreen instanceof nekoplusGui)) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        mc.entityRenderer.stopUseShader();
        if(mc.currentScreen instanceof nekoplusGui) {
            mc.displayGuiScreen(null);
        }
    }

    public void shaderOn() {
        if(!shader.getValue()) {
            mc.entityRenderer.stopUseShader();
        }
    }
}
