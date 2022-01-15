
package me.vaimok.nekoplus.client.gui.components.items.buttons;

import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.modules.client.ClickGui;
import me.vaimok.nekoplus.api.util.moduleUtil.ColorUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.MathUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

public class StringButton extends Button
{
    private Setting setting;
    public boolean isListening;
    private CurrentString currentString;

    public StringButton(final Setting setting) {
        super(setting.getName());
        this.currentString = new CurrentString("");
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {

            RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()) : nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        if (this.isListening) {
            nekoplus.textManager.drawStringWithShadow(this.currentString.getString() + nekoplus.textManager.getIdleSign(), this.x + 2.3f, this.y - 1.7f - me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
        else {
            nekoplus.textManager.drawStringWithShadow((this.setting.shouldRenderName() ? (this.setting.getName() + " ") : "") + this.setting.getValue(), this.x + 2.3f, this.y - 1.7f - me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            StringButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            if (keyCode == 1) {
                return;
            }
            if (keyCode == 28) {
                this.enterString();
            }
            else if (keyCode == 14) {
                this.setString(removeLastChar(this.currentString.getString()));
            }
            else {
                Label_0122: {
                    if (keyCode == 47) {
                        if (!Keyboard.isKeyDown(157)) {
                            if (!Keyboard.isKeyDown(29)) {
                                break Label_0122;
                            }
                        }
                        try {
                            this.setString(this.currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    this.setString(this.currentString.getString() + typedChar);
                }
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        }
        else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        super.onMouseClick();
    }

    @Override
    public float getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }

    public void setString(final String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(final String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public static class CurrentString
    {
        private String string;

        public CurrentString(final String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
