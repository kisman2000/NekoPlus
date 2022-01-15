package me.vaimok.nekoplus.client.modules.client;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.event.events.Render2DEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import me.vaimok.nekoplus.api.util.moduleUtil.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.*;

public
class HUD extends Module {

    private static final ResourceLocation box = new ResourceLocation ( "textures/gui/container/shulker_box.png" );
    private static final ItemStack totem = new ItemStack ( Items.TOTEM_OF_UNDYING );
    private static HUD INSTANCE = new HUD ( );
    private final Setting < Boolean > watermark = register ( new Setting ( "Watermark" , false , "WaterMark" ) );
    private final Setting < Boolean > arrayList = register ( new Setting ( "ArrayList" , false , "Lists the active modules." ) );
    private final Setting < Boolean > serverBrand = register ( new Setting ( "ServerBrand" , false , "Brand of the server you are on." ) );
    private final Setting < Boolean > ping = register ( new Setting ( "Ping" , false , "Your response time to the server." ) );
    private final Setting < Boolean > tps = register ( new Setting ( "TPS" , false , "Ticks per second of the server." ) );
    private final Setting < Boolean > fps = register ( new Setting ( "FPS" , false , "Your frames per second." ) );
    private final Setting < Boolean > coords = register ( new Setting ( "Coords" , false , "Your current coordinates" ) );
    private final Setting < Boolean > direction = register ( new Setting ( "Direction" , false , "The Direction you are facing." ) );
    private final Setting < Boolean > speed = register ( new Setting ( "Speed" , false , "Your Speed" ) );
    private final Setting < Boolean > potions = register ( new Setting ( "Potions" , false , "Your Speed" ) );
    private final Setting < Boolean > armor = register ( new Setting ( "Armor" , false , "ArmorHUD" ) );
    private final Setting < Boolean > percent = register ( new Setting ( "Percent" , false , v -> armor.getValue ( ) ) );
    private final Setting < Boolean > totems = register ( new Setting ( "Totems" , false , "TotemHUD" ) );
    private final Setting < Greeter > greeter = register ( new Setting ( "Greeter" , Greeter.NONE , "Greets you." ) );
    private final Timer timer = new Timer ( );
    private final boolean shadow = true;
    public Setting < Boolean > colorSync = register ( new Setting ( "ColorSync" , false ) );
    public Setting < Boolean > rainbow = register ( new Setting ( "Rainbow" , false , v -> ! colorSync.getValue ( ) ) );
    public Setting < Integer > rainbowSpeed = register ( new Setting ( "Speed" , 70 , 0 , 400 , v -> rainbow.getValue ( ) && ! colorSync.getValue ( ) ) );
    public Setting < Boolean > potionIcons = register ( new Setting ( "RemovePotionIcons" , true , "Draws Potion Icons." ) );
    public Setting < Boolean > textRadar = register ( new Setting ( "TextRadar" , false , "A TextRadar" ) );
    public Setting < Boolean > time = register ( new Setting ( "Time" , false , "The time" ) );
    public Setting < Integer > hudRed = register ( new Setting ( "Red" , 255 , 0 , 255 ) );
    public Setting < Integer > hudGreen = register ( new Setting ( "Green" , 0 , 0 , 255 ) );
    public Setting < Integer > hudBlue = register ( new Setting ( "Blue" , 0 , 0 , 255 ) );
    private Map < String, Integer > players = new HashMap <> ( );
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;

    public
    HUD ( ) {
        super ( "HUD" , "HUD Elements rendered on your screen" , Category.CLIENT , true , false , false );
        setInstance ( );
    }

    public static
    HUD getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new HUD ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onUpdate ( ) {
        if ( timer.passedMs ( Managers.getInstance ( ).textRadarUpdates.getValue ( ) ) ) {
            this.players = getTextRadarPlayers ( );
            timer.reset ( );
        }
        if ( shouldIncrement ) {
            hitMarkerTimer++;
        }
        if ( hitMarkerTimer == 10 ) {
            hitMarkerTimer = 0;
            shouldIncrement = false;
        }
    }

    @Override
    public
    void onRender2D ( Render2DEvent event ) {
        if ( fullNullCheck ( ) ) {
            return;
        }

        int i;
        String grayString;
        String str;
        int j;
        float f;
        char[] stringToCharArray;
        int[] arrayOfInt;
        String string;

        int width = renderer.scaledWidth;
        int height = renderer.scaledHeight;
        color = this.colorSync.getValue ( ) ? ColorUtil.toARGB ( Colors.INSTANCE.getCurrentColor ( ).getRed ( ) , Colors.INSTANCE.getCurrentColor ( ).getGreen ( ) , Colors.INSTANCE.getCurrentColor ( ).getBlue ( ) , 255 ) : ColorUtil.toRGBA ( hudRed.getValue ( ) , hudGreen.getValue ( ) , hudBlue.getValue ( ) );
        String whiteString = TextUtil.WHITE;

        if ( watermark.getValue ( ) ) {
            arrayOfInt = new int[]{1};
            string = nekoplus.getName ( ) + " v" + nekoplus.MODVER;
            stringToCharArray = string.toCharArray ( );
            f = 0.0f;
            for (char c : stringToCharArray) {
                this.renderer.drawString ( String.valueOf ( c ) , 2.0f + f , 2 , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
        }

        j = 0;
        if ( arrayList.getValue ( ) ) {
            arrayOfInt = new int[]{1};
            f = 0.0f;
            for (i = 0; i < nekoplus.moduleManager.sortedModules.size ( ); i++) {
                Module module = nekoplus.moduleManager.sortedModules.get ( i );
                String text = module.getDisplayName ( ) + ( module.getDisplayInfo ( ) != null ? " [" + TextUtil.WHITE + module.getDisplayInfo ( ) + TextUtil.RESET + "]" : "" );
                //renderer.drawString(text, width - 2 - renderer.getStringWidth(text), 2 + j * 10, color, shadow);
                this.renderer.drawString ( text , width - 2 - renderer.getStringWidth ( text ) , 2 + j * 10 , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                arrayOfInt[0] = arrayOfInt[0] + 1;
                j++;
            }
        }

        i = mc.currentScreen instanceof GuiChat ? 14 : 0;
        if ( serverBrand.getValue ( ) ) {
            String text = "Server brand " + nekoplus.serverManager.getServerBrand ( );
            arrayOfInt = new int[]{1};
            stringToCharArray = text.toCharArray ( );
            f = 0.0f;
            i += 10;
            for (char c : stringToCharArray) {
                this.renderer.drawString ( String.valueOf ( c ) , width - ( renderer.getStringWidth ( text ) ) + f - 2 , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
        }
        if ( potions.getValue ( ) ) {
            String text;
            List < String > effects = new ArrayList <> ( );
            for (PotionEffect effect : nekoplus.potionManager.getOwnPotions ( )) {
                text = nekoplus.potionManager.getPotionString ( effect );
                effects.add ( text );
            }
            Collections.sort ( effects , Comparator.comparing ( String::length ) );

            for (int x = effects.size ( ) - 1; x >= 0; x--) {
                i += 10;
                text = effects.get ( x );
                arrayOfInt = new int[]{1};
                f = 0.0f;
                stringToCharArray = text.toCharArray ( );
                for (char c : stringToCharArray) {
                    this.renderer.drawString ( String.valueOf ( c ) , width - ( renderer.getStringWidth ( text ) ) + f - 2 , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                    f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                    arrayOfInt[0] = arrayOfInt[0] + 1;
                }
            }
        }
        if ( speed.getValue ( ) ) {
            String text = "Speed " + nekoplus.speedManager.getSpeedKpH ( ) + " km/h";
            arrayOfInt = new int[]{1};
            stringToCharArray = text.toCharArray ( );
            f = 0.0f;
            i += 10;
            for (char c : stringToCharArray) {
                this.renderer.drawString ( String.valueOf ( c ) , width - ( renderer.getStringWidth ( text ) ) + f - 2 , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
        }
        if ( time.getValue ( ) ) {
            String text = "Time " + ( new SimpleDateFormat ( "h:mm a" ).format ( new Date ( ) ) );
            arrayOfInt = new int[]{1};
            stringToCharArray = text.toCharArray ( );
            f = 0.0f;
            i += 10;
            for (char c : stringToCharArray) {
                this.renderer.drawString ( String.valueOf ( c ) , width - ( renderer.getStringWidth ( text ) ) + f - 2 , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
        }
        if ( tps.getValue ( ) ) {
            String text = "TPS " + nekoplus.serverManager.getTPS ( );
            arrayOfInt = new int[]{1};
            stringToCharArray = text.toCharArray ( );
            f = 0.0f;
            i += 10;
            for (char c : stringToCharArray) {
                this.renderer.drawString ( String.valueOf ( c ) , width - ( renderer.getStringWidth ( text ) ) + f - 2 , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
        }
        String fpsText = "FPS " + Minecraft.debugFPS;
        String text = "Ping " + nekoplus.serverManager.getPing ( );
        if ( fps.getValue ( ) ) {
            arrayOfInt = new int[]{1};
            stringToCharArray = fpsText.toCharArray ( );
            f = 0.0f;
            i += 10;
            for (char c : stringToCharArray) {
                this.renderer.drawString ( String.valueOf ( c ) , width - ( renderer.getStringWidth ( fpsText ) ) + f - 2 , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
        }
        if ( ping.getValue ( ) ) {
            arrayOfInt = new int[]{1};
            stringToCharArray = text.toCharArray ( );
            f = 0.0f;
            i += 10;
            for (char c : stringToCharArray) {
                this.renderer.drawString ( String.valueOf ( c ) , width - ( renderer.getStringWidth ( text ) ) + f - 2 , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
                f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
        }

        boolean inHell = ( mc.world.getBiome ( mc.player.getPosition ( ) ).getBiomeName ( ).equals ( "Hell" ) );

        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;

        float nether = ! inHell ? 0.125f : 8;
        int hposX = (int) ( mc.player.posX * nether );
        int hposZ = (int) ( mc.player.posZ * nether );

        nekoplus.notificationManager.handleNotifications ( height - ( i + 16 ) );

        i = mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]";
        text = ( direction.getValue ( ) ? nekoplus.rotationManager.getDirection4D ( false ) + " " : "" ) + ( coords.getValue ( ) ? coordinates : "" ) + "";

        arrayOfInt = new int[]{1};
        stringToCharArray = text.toCharArray ( );
        f = 0.0f;
        i += 10;
        for (char c : stringToCharArray) {
            this.renderer.drawString ( String.valueOf ( c ) , 2 + f , height - ( i ) , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
            f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
            arrayOfInt[0] = arrayOfInt[0] + 1;
        }

        if ( armor.getValue ( ) ) {
            renderArmorHUD ( percent.getValue ( ) );
        }

        if ( totems.getValue ( ) ) {
            renderTotemHUD ( );
        }

        if ( greeter.getValue ( ) != Greeter.NONE ) {
            renderGreeter ( );
        }
    }

    public
    Map < String, Integer > getTextRadarPlayers ( ) {
        return EntityUtil.getTextRadarPlayers ( );
    }

    public
    void renderGreeter ( ) {
        int width = renderer.scaledWidth;
        String text = "";
        switch (greeter.getValue ( )) {
            case TIME:
                text += MathUtil.getTimeOfDay ( ) + mc.player.getDisplayNameString ( );
                break;
            case LONG:
                text += "Nya, ur hot " + mc.player.getDisplayNameString ( ) + " :)";
                break;
            default:
                text += "Nya " + mc.player.getDisplayNameString ( );
        }
        //renderer.drawString(text, (width / 2.0f) - (renderer.getStringWidth(text) / 2.0f) + 2, 2, color, shadow);
        float f;
        char[] stringToCharArray;
        int[] arrayOfInt;

        arrayOfInt = new int[]{1};
        stringToCharArray = text.toCharArray ( );
        f = 0.0f;
        for (char c : stringToCharArray) {
            this.renderer.drawString ( String.valueOf ( c ) , ( width / 2.0f ) - ( renderer.getStringWidth ( text ) / 2.0f ) + 2 + f , 2 , this.rainbow.getValue ( ) ? ColorUtil.rainbow ( arrayOfInt[0] * HUD.getInstance ( ).rainbowSpeed.getValue ( ) ).getRGB ( ) : color , true );
            f += (float) this.renderer.getStringWidth ( String.valueOf ( c ) );
            arrayOfInt[0] = arrayOfInt[0] + 1;
        }
    }

    public
    void renderTotemHUD ( ) {
        int width = renderer.scaledWidth;
        int height = renderer.scaledHeight;
        int totems = mc.player.inventory.mainInventory.stream ( ).filter ( itemStack -> itemStack.getItem ( ) == Items.TOTEM_OF_UNDYING ).mapToInt ( ItemStack::getCount ).sum ( );
        if ( mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.TOTEM_OF_UNDYING )
            totems += mc.player.getHeldItemOffhand ( ).getCount ( );
        if ( totems > 0 ) {
            GlStateManager.enableTexture2D ( );
            int i = width / 2;
            int iteration = 0;
            int y = height - 55 - ( mc.player.isInWater ( ) && mc.playerController.gameIsSurvivalOrAdventure ( ) ? 10 : 0 );
            int x = i - 189 + 9 * 20 + 2;
            GlStateManager.enableDepth ( );
            RenderUtil.itemRender.zLevel = 200F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI ( totem , x , y );
            RenderUtil.itemRender.renderItemOverlayIntoGUI ( mc.fontRenderer , totem , x , y , "" );
            RenderUtil.itemRender.zLevel = 0F;
            GlStateManager.enableTexture2D ( );
            GlStateManager.disableLighting ( );
            GlStateManager.disableDepth ( );
            renderer.drawStringWithShadow ( totems + "" , x + 19 - 2 - renderer.getStringWidth ( totems + "" ) , y + 9 , 0xffffff );
            //mc.fontRenderer.drawStringWithShadow(totems + "", x + 19 - 2 - mc.fontRenderer.getStringWidth(totems + ""), y + 9, 0xffffff);
            GlStateManager.enableDepth ( );
            GlStateManager.disableLighting ( );
        }
    }

    public
    void renderArmorHUD ( boolean percent ) {
        int width = renderer.scaledWidth;
        int height = renderer.scaledHeight;
        GlStateManager.enableTexture2D ( );
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - ( mc.player.isInWater ( ) && mc.playerController.gameIsSurvivalOrAdventure ( ) ? 10 : 0 );
        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if ( is.isEmpty ( ) ) continue;
            int x = i - 90 + ( 9 - iteration ) * 20 + 2;
            GlStateManager.enableDepth ( );
            RenderUtil.itemRender.zLevel = 200F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI ( is , x , y );
            RenderUtil.itemRender.renderItemOverlayIntoGUI ( mc.fontRenderer , is , x , y , "" );
            RenderUtil.itemRender.zLevel = 0F;
            GlStateManager.enableTexture2D ( );
            GlStateManager.disableLighting ( );
            GlStateManager.disableDepth ( );
            String s = is.getCount ( ) > 1 ? is.getCount ( ) + "" : "";
            renderer.drawStringWithShadow ( s , x + 19 - 2 - renderer.getStringWidth ( s ) , y + 9 , 0xffffff );
            //mc.fontRenderer.drawStringWithShadow(s, x + 19 - 2 - mc.fontRenderer.getStringWidth(s), y + 9, 0xffffff);

            if ( percent ) {
                int dmg = 0;
                int itemDurability = is.getMaxDamage ( ) - is.getItemDamage ( );
                float green = ( (float) is.getMaxDamage ( ) - (float) is.getItemDamage ( ) ) / (float) is.getMaxDamage ( );
                float red = 1 - green;
                if ( percent ) {
                    dmg = 100 - (int) ( red * 100 );
                } else {
                    dmg = itemDurability;
                }
                renderer.drawStringWithShadow ( dmg + "" , x + 8 - renderer.getStringWidth ( dmg + "" ) / 2 , y - 11 , ColorUtil.toRGBA ( (int) ( red * 255 ) , (int) ( green * 255 ) , 0 ) );
            }
        }
        GlStateManager.enableDepth ( );
        GlStateManager.disableLighting ( );
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( AttackEntityEvent event ) {
        shouldIncrement = true;
    }


    public
    enum Greeter {
        NONE,
        TIME,
        LONG
    }
}
