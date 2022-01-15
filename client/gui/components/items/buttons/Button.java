package me.vaimok.nekoplus.client.gui.components.items.buttons;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.gui.components.Component;
import me.vaimok.nekoplus.client.gui.components.items.Item;
import me.vaimok.nekoplus.client.modules.client.ClickGui;
import me.vaimok.nekoplus.api.util.moduleUtil.ColorUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.MathUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class Button extends Item {
    private ResourceLocation logo = new ResourceLocation("textures/geaer.png");
    public Map<Integer, Integer> colorMap;

    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()) : nekoplus.colorManager.getColorWithAlpha(nekoplus.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        nekoplus.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        //RenderUtil.drawBorderedRect(x + 2, y + 2, x+width + 2, y + height - 0.5f + 2, 0.8, new Color(0, 0, 0, 0).getRGB(), new Color(62, 62, 62, 220).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            onMouseClick();
        }
    }

    public void onMouseClick() {
        state = !state;
        toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void toggle() {}

    public boolean getState() {
        return state;
    }

    @Override
    public float getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : me.vaimok.nekoplus.client.gui.nekoplusGui.getClickGui().getComponents()) {
            if (component.drag) {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}
