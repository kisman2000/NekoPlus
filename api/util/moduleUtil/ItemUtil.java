package me.vaimok.nekoplus.api.util.moduleUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

import java.util.Iterator;

public
class ItemUtil
        implements Minecraftable {
    public static
    int getItemFromHotbar ( Item item ) {
        int slot = - 1;
        for (int i = 0; i < 9; ++ i) {
            ItemStack stack = ItemUtil.mc.player.inventory.getStackInSlot ( i );
            if ( stack.getItem ( ) != item ) continue;
            slot = i;
        }
        return slot;
    }

    public static
    int getItemSlot ( Class clss ) {
        int itemSlot = - 1;
        for (int i = 45; i > 0; -- i) {
            if ( ItemUtil.mc.player.inventory.getStackInSlot ( i ).getItem ( ).getClass ( ) != clss ) continue;
            itemSlot = i;
            break;
        }
        return itemSlot;
    }

    public static
    int getItemSlot ( Item item ) {
        int itemSlot = - 1;
        for (int i = 45; i > 0; -- i) {
            if ( ! ItemUtil.mc.player.inventory.getStackInSlot ( i ).getItem ( ).equals ( item ) ) continue;
            itemSlot = i;
            break;
        }
        return itemSlot;
    }

    public static
    int getItemCount ( Item item ) {
        int count = 0;
        int size = ItemUtil.mc.player.inventory.mainInventory.size ( );
        for (int i = 0; i < size; ++ i) {
            ItemStack itemStack = (ItemStack) ItemUtil.mc.player.inventory.mainInventory.get ( i );
            if ( itemStack.getItem ( ) != item ) continue;
            count += itemStack.getCount ( );
        }
        ItemStack offhandStack = ItemUtil.mc.player.getHeldItemOffhand ( );
        if ( offhandStack.getItem ( ) == item ) {
            count += offhandStack.getCount ( );
        }
        return count;
    }

    public static
    boolean isArmorLow ( EntityPlayer player , int durability ) {
        Iterator iterator = player.inventory.armorInventory.iterator ( );
        while ( iterator.hasNext ( ) ) {
            ItemStack piece = (ItemStack) iterator.next ( );
            if ( piece != null && ! ( ItemUtil.getDamageInPercent ( piece ) < (float) durability ) ) continue;
            return true;
        }
        return false;
    }

    public static
    int getItemDamage ( ItemStack stack ) {
        return stack.getMaxDamage ( ) - stack.getItemDamage ( );
    }

    public static
    float getDamageInPercent ( ItemStack stack ) {
        float green = ( (float) stack.getMaxDamage ( ) - (float) stack.getItemDamage ( ) ) / (float) stack.getMaxDamage ( );
        float red = 1.0f - green;
        return 100 - (int) ( red * 100.0f );
    }

    public static
    int getRoundedDamage ( ItemStack stack ) {
        return (int) ItemUtil.getDamageInPercent ( stack );
    }

    public static
    boolean hasDurability ( ItemStack stack ) {
        Item item = stack.getItem ( );
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }
}