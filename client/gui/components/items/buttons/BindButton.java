package me.vaimok.nekoplus.client.gui.components.items.buttons;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.client.modules.client.ClickGui;
import me.vaimok.nekoplus.client.setting.Bind;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button {

    private Setting setting;
    public boolean isListening;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? nekoplus.colorManager.getColorWithAlpha(((ClickGui) nekoplus.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue()) : nekoplus.colorManager.getColorWithAlpha(((ClickGui) nekoplus.moduleManager.getModuleByName("ClickGui")).alpha.getValue())) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        if (isListening) {
            nekoplus.textManager.drawStringWithShadow("Listening...", x + 2.3F, y - 1.7F - me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        } else {
            nekoplus.textManager.drawStringWithShadow(setting.getName() + " " + TextUtil.GRAY + ((Bind) setting.getValue()).toString(), x + 2.3F, y - 1.7F - me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if(isListening) {
            Bind bind = new Bind(keyCode);
            if(bind.toString().equalsIgnoreCase("Escape")) {
                return;
            } else if(bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            setting.setValue(bind);
            super.onMouseClick();
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    public void toggle() {
        isListening = !isListening;
    }

    public boolean getState() {
        return !isListening;
    }
}
