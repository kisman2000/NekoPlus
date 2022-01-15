

package me.vaimok.nekoplus.client.gui.components.items.buttons;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.modules.client.ClickGui;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.ColorUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.MathUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.TextUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton extends Button
{
    public Setting setting;

    public EnumButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
            RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()) : nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        nekoplus.textManager.drawStringWithShadow(setting.getName() + " " + TextUtil.GRAY + setting.currentEnumName(), x + 2.3F, y - 1.7F - me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            EnumButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.increaseEnum();
    }

    @Override
    public boolean getState() {
        return true;
    }
}
