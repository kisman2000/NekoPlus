

package me.vaimok.nekoplus.client.gui.components.items.buttons;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.client.modules.client.ClickGui;
import me.vaimok.nekoplus.api.util.moduleUtil.ColorUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.MathUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider extends Button
{
    public Setting setting;

    public UnlimitedSlider(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
            RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()) : nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()));
        nekoplus.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + "" + this.setting.getValue() + "" + " +", this.x + 2.3f, this.y - 1.7f - me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            UnlimitedSlider.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.isRight(mouseX)) {
                if (this.setting.getValue() instanceof Double) {
                    this.setting.setValue((double)this.setting.getValue() + 1.0);
                }
                else if (this.setting.getValue() instanceof Float) {
                    this.setting.setValue((float)this.setting.getValue() + 1.0f);
                }
                else if (this.setting.getValue() instanceof Integer) {
                    this.setting.setValue((int)this.setting.getValue() + 1);
                }
            }
            else if (this.setting.getValue() instanceof Double) {
                this.setting.setValue((double)this.setting.getValue() - 1.0);
            }
            else if (this.setting.getValue() instanceof Float) {
                this.setting.setValue((float)this.setting.getValue() - 1.0f);
            }
            else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue((int)this.setting.getValue() - 1);
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public float getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
    }

    @Override
    public boolean getState() {
        return true;
    }

    public boolean isRight(final int x) {
        return x > this.x + (this.width + 7.4f) / 2.0f;
    }
}
