package me.vaimok.nekoplus.client.gui;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.gui.components.Component;
import me.vaimok.nekoplus.client.gui.components.items.Item;
import me.vaimok.nekoplus.client.gui.components.items.buttons.ModuleButton;
import me.vaimok.nekoplus.client.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class nekoplusGui extends GuiScreen {

    private ResourceLocation misc = new ResourceLocation("textures/misc.png");
    private ResourceLocation world = new ResourceLocation("textures/world.png");
    private ResourceLocation combat = new ResourceLocation("textures/combat.png");
    private ResourceLocation chat = new ResourceLocation("textures/chat.png");
    private ResourceLocation movement = new ResourceLocation("textures/movement.png");
    private ResourceLocation render = new ResourceLocation("textures/render.png");
    private ResourceLocation nekoplusimg = new ResourceLocation("textures/nekoplus.png");
    private static me.vaimok.nekoplus.client.gui.nekoplusGui nekoplusGui;
    private final ArrayList<Component> components = new ArrayList<>();
    private static me.vaimok.nekoplus.client.gui.nekoplusGui INSTANCE = new me.vaimok.nekoplus.client.gui.nekoplusGui();

    private boolean state;

    public nekoplusGui() {
        setInstance();
        load();
    }

    public static me.vaimok.nekoplus.client.gui.nekoplusGui getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new me.vaimok.nekoplus.client.gui.nekoplusGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static me.vaimok.nekoplus.client.gui.nekoplusGui getClickGui() {
        return getInstance();
    }

    private void load() {
        int x = -84;
        for(Module.Category category : nekoplus.moduleManager.getCategories()) {
            components.add(new Component(category.getName(), x += 115 , 4, true) {
                @Override
                public void setupItems() {
                    me.vaimok.nekoplus.nekoplus.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        components.forEach(components -> components.getItems().sort((item1, item2) -> item1.getName().compareTo(item2.getName())));
    }

    /*public void imageDraw() {
        int x = -84;
        // MISC
        Wrapper.getMinecraft().renderEngine.bindTexture(misc);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableTexture2D();
        RenderUtil.drawTexture( x += 115, 30, 20, 20, 0, 0, 1, 1);
        // WORLD
        Wrapper.getMinecraft().renderEngine.bindTexture(world);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableTexture2D();
        RenderUtil.drawTexture( x += 115, 30, 20, 20, 0, 0, 1, 1);
        // COMBAT
        Wrapper.getMinecraft().renderEngine.bindTexture(combat);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableTexture2D();
        RenderUtil.drawTexture( x += 115, 30, 20, 20, 0, 0, 1, 1);
        // RENDER
        Wrapper.getMinecraft().renderEngine.bindTexture(render);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableTexture2D();
        RenderUtil.drawTexture( x += 115, 30, 20, 20, 0, 0, 1, 1);
        // MOVEMENT
        Wrapper.getMinecraft().renderEngine.bindTexture(movement);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableTexture2D();
        RenderUtil.drawTexture( x += 115, 30, 20, 20, 0, 0, 1, 1);
        // CLIENT
        Wrapper.getMinecraft().renderEngine.bindTexture(moonhack);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableTexture2D();
        RenderUtil.drawTexture( x += 115, 30, 20, 20, 0, 0, 1, 1);
        //CHAT
        Wrapper.getMinecraft().renderEngine.bindTexture(chat);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableTexture2D();
        RenderUtil.drawTexture( x += 115, 30, 20, 20, 0, 0, 1, 1);
    }

    public static void drawImgAction(int posX, int posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0F);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, height, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(width, height, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(width, 0.0F, 0.0F);
        GL11.glEnd();
        GL11.glPopMatrix();
    }*/


    public void updateModule(Module module) {
        for(Component component : this.components) {
            for(Item item : component.getItems()) {
                if(item instanceof ModuleButton) {
                    ModuleButton button = (ModuleButton)item;
                    Module mod = button.getModule();
                    if(module != null && module.equals(mod)) {
                        button.initSettings();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        checkMouseWheel();
        components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
    }

 /*   public void drawPlayer() {
        final EntityPlayer ent = mc.player;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(MoonHack.moduleManager.getModule(ClickGui.class).playerX.getValue() + 25, MoonHack.moduleManager.getModule(ClickGui.class).playerY.getValue() + 25, 50.0f);
        GlStateManager.scale(-50.0f * MoonHack.moduleManager.getModule(ClickGui.class).playerScale.getValue(), 50.0f * MoonHack.moduleManager.getModule(ClickGui.class).playerScale.getValue(), 50.0f * MoonHack.moduleManager.getModule(ClickGui.class).playerScale.getValue());
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float)Math.atan(MoonHack.moduleManager.getModule(ClickGui.class).playerY.getValue() / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }*/

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return (int) -5.9;
    }


    public Component getComponentByName(String name) {
        for(Component component : this.components) {
            if(component.getName().equalsIgnoreCase(name)) {
                return component;
            }
        }
        return null;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

}