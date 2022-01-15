package me.vaimok.nekoplus.client.modules.player;

import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.client.modules.Module;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

import java.util.List;

public class PacketMend extends Module {

    public Setting < Boolean > sneakOnly = this.register ( new Setting < Boolean > ( "SneakOnly", true ));
    public Setting < Boolean > noEntityCollision = this.register( new Setting ("No Collision", true));
    public Setting < Boolean > silentSwitch = this.register( new Setting ("Silent Switch", true));
    public Setting < Integer > minDamage = this.register( new Setting ("Min Damage", 50, 1, 100));
    public Setting < Integer > maxHeal = this.register( new Setting ("Repair To", 90, 1, 100));
    public Setting < Boolean > predict = this.register( new Setting ("predict", false));
    public Setting < Boolean > DisableWhenDone = this.register( new Setting ("AutoDisable", true));

public PacketMend() {super("PacketMend", "mends", Category.PLAYER, true, false, false);}

    char toMend = 0;

    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.ticksExisted < 10) {
            return;
        }

        int sumOfDamage = 0;

        List<ItemStack> armour = mc.player.inventory.armorInventory;
        for (int i = 0; i < armour.size(); i++) {
            ItemStack itemStack = armour.get(i);
            if (itemStack.isEmpty) {
                continue;
            }

            //this works better than my calculation for some reason, thank you ArmorHUD.java
            float damageOnArmor = (float) (itemStack.getMaxDamage() - itemStack.getItemDamage());
            float damagePercent = 100 - (100 * (1 - damageOnArmor / itemStack.getMaxDamage()));

            if (damagePercent <= maxHeal.getValue()) {
                if (damagePercent <= minDamage.getValue()) {
                    toMend |= 1 << i;

                }
                if (damagePercent <= maxHeal.getValue() && DisableWhenDone.getValue()) {
                    toggle();
                }
                if (predict.getValue()) {
                    sumOfDamage += (itemStack.getMaxDamage() * maxHeal.getValue() / 100f) - (itemStack.getMaxDamage() - itemStack.getItemDamage());
                }
            } else {
                toMend &= ~(1 << i);
            }
        }

        if (toMend > 0) {
            if (predict.getValue()) {
                // get all the xp orbs on top of us
                int totalXp = mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityXPOrb)
                        .filter(entity -> entity.getDistanceSq(mc.player) <= 1)
                        .mapToInt(entity -> ((EntityXPOrb) entity).xpValue).sum();

                // see EntityXpOrbxpToDurability(int xp)
                if ((totalXp * 2) < sumOfDamage) {
                    mendArmor(mc.player.inventory.currentItem);
                }
            } else {
                mendArmor(mc.player.inventory.currentItem);
            }
        }
    }

    private void mendArmor(int oldSlot) {
        if (noEntityCollision.getValue()) {
            for (EntityPlayer entityPlayer : mc.world.playerEntities) {
                if (entityPlayer.getDistance(mc.player) < 1 && entityPlayer != mc.player) {
                    return;
                }
            }
        }

        if (sneakOnly.getValue() && !mc.player.isSneaking()) {
            return;
        }

        int newSlot = findXPSlot();

        if (newSlot == -1) {
            return;
        }

        if (oldSlot != newSlot) {
            if(silentSwitch.getValue()){
                mc.player.connection.sendPacket(new CPacketHeldItemChange(newSlot));
            }
            else {
                mc.player.inventory.currentItem = newSlot;
            }
            mc.playerController.syncCurrentPlayItem();
        }

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, 90, true));
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if(silentSwitch.getValue()){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
        else {
            mc.player.inventory.currentItem = oldSlot;
        }
        mc.playerController.syncCurrentPlayItem();
    }

    private int findXPSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }

        return slot;
    }
}