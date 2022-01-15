package me.vaimok.nekoplus.client.gui.components.items;

import me.vaimok.nekoplus.client.Client;

public class Item extends Client {

    protected float x, y;
    protected float width, height;
    private boolean hidden;

    public Item(String name) {
        super(name);
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {}

    public void update() {}

    public void onKeyTyped(char typedChar, int keyCode) {}

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean setHidden(boolean hidden) {
        return this.hidden = hidden;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
