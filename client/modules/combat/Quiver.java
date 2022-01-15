package me.vaimok.nekoplus.client.modules.combat;

import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.*;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Quiver extends Module {
    private final Setting < Integer > delay = register ( new Setting( "Delay" , 200 , 50 , 500 ) );
    private final Setting < Integer > holdLength = register ( new Setting ( "Hold Length" , 350 , 0 , 2000 ) );
    private final Setting < mainEnum > main = this.register ( new Setting < Object > ( "Main" , mainEnum.SPEED));
    private final Setting < mainEnum > secondary = this.register ( new Setting < Object > ( "Secondary" , mainEnum.STRENGTH));
    private final Timer delayTimer = new Timer();
    private final Timer holdTimer = new Timer();
    private int stage = 0;
    private ArrayList<Integer> map;
    private int strSlot = -1;
    private int speedSlot = -1;
    private int oldSlot = 1;

    public Quiver ( ) {
        super ( "Quiver" , "Puts Armor on for you." , Category.COMBAT , true , false , false );
    }

    private enum mainEnum {
        STRENGTH, SPEED
    }

    private enum secondaryEnum {
        STRENGTH, SPEED
    }

    @Override
    public void onEnable() {
        if (nullCheck()) return;
        InventoryUtil.switchTo(Items.BOW);
        clean();
        this.oldSlot = mc.player.inventory.currentItem;
        mc.gameSettings.keyBindUseItem.pressed = false;
    }

    @Override
    public void onDisable() {
        if (nullCheck()) return;
        InventoryUtil.switchToSlot(oldSlot);
        mc.gameSettings.keyBindUseItem.pressed = false;
        clean();
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        if (mc.currentScreen != null) return;

        if (InventoryUtil.find(Items.BOW) == -1) {
            toggleWithMessage("Couldn't find bow in inventory! Toggling!");
            return;
        }

        RotationUtil.faceVector(EntityUtil.getInterpolatedPos(mc.player, mc.timer.elapsedPartialTicks).add(0, 3, 0), false);

        if (stage == 0) { // stage one is mapping arrows
            this.map = mapArrows();
            for (int a : map) {
                final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
                    if (strSlot == -1) {
                        this.strSlot = a;
                    }
                }
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
                    if (speedSlot == -1) {
                        this.speedSlot = a;
                    }
                }
            }
            this.stage++;
        } else if (stage == 1) { // wait
            if (!delayTimer.passedMs(delay.getValue())) return;
            delayTimer.reset();
            this.stage++;
        } else if (stage == 2) { // switch
            switchTo(main.getValue());
            this.stage++;
        } else if (stage == 3) { // wait
            if (!delayTimer.passedMs(delay.getValue())) return;
            delayTimer.reset();
            this.stage++;
        } else if (stage == 4) { // charge
            mc.gameSettings.keyBindUseItem.pressed = true;
            holdTimer.reset();
            this.stage++;
        } else if (stage == 5) { // wait
            if (!holdTimer.passedMs(holdLength.getValue())) return;
            holdTimer.reset();
            this.stage++;
        } else if (stage == 6) { // shoot
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.resetActiveHand();
            mc.gameSettings.keyBindUseItem.pressed = false;
            this.stage++;
        } else if (stage == 7) { // wait
            if (!delayTimer.passedMs(delay.getValue())) return;
            delayTimer.reset();
            this.stage++;
        } else if (stage == 8) { // map again
            this.map = mapArrows();
            this.strSlot = -1;
            this.speedSlot = -1;
            for (int a : map) {
                final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
                    if (strSlot == -1)
                        this.strSlot = a;
                }
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
                    if (speedSlot == -1)
                        this.speedSlot = a;
                }
            }
            this.stage++;
        }
        if (stage == 9) { // switch
            switchTo(secondary.getValue());
            this.stage++;
        } else if (stage == 10) { // wait
            if (!delayTimer.passedMs(delay.getValue())) return;
            this.stage++;
        } else if (stage == 11) { // charge
            mc.gameSettings.keyBindUseItem.pressed = true;
            holdTimer.reset();
            this.stage++;
        } else if (stage == 12) { // wait
            if (!holdTimer.passedMs(holdLength.getValue())) return;
            holdTimer.reset();
            this.stage++;
        } else if (stage == 13) { // shoot
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.resetActiveHand();
            mc.gameSettings.keyBindUseItem.pressed = false;
            this.stage++;
        } else if (stage == 14) { // fix for still holding item
            final ArrayList<Integer> map = mapEmpty();
            if(!map.isEmpty()) {
                final int a = map.get(0);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, a, 0, ClickType.PICKUP, mc.player);
            }
            this.stage++;
        } else if (stage == 15) { // clean up
            this.setEnabled(false);
        }
    }

    private void switchTo(Enum mode) {
        if (mode.toString().equalsIgnoreCase("STRENGTH")) {
            if (strSlot != -1) {
                switchTo(strSlot);
            }
        }
        if (mode.toString().equalsIgnoreCase("SPEED")) {
            if (speedSlot != -1) {
                switchTo(speedSlot);
            }
        }
    }

    private ArrayList<Integer> mapArrows() {
        ArrayList<Integer> map = new ArrayList<>();
        for (int a = 9; a < 45; a++) {
            if (mc.player.inventoryContainer.getInventory().get(a).getItem() instanceof ItemTippedArrow) {
                final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
                    map.add(a);
                }
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
                    map.add(a);
                }
            }
        }
        return map;
    }

    private ArrayList<Integer> mapEmpty() {
        ArrayList<Integer> map = new ArrayList<>();
        for (int a = 9; a < 45; a++) {
            if (mc.player.inventoryContainer.getInventory().get(a).getItem() instanceof ItemAir || mc.player.inventoryContainer.getInventory().get(a) == ItemStack.EMPTY) {
                map.add(a);
            }
        }
        return map;
    }

    private void switchTo(int from) {
        if(from == 9) return;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    private boolean hasEffect(String mode) {
        if (mode.equalsIgnoreCase("Strength")) {
            if (mc.player.isPotionActive(MobEffects.STRENGTH)) return true;
        }
        if (mode.equalsIgnoreCase("Speed")) {
            return mc.player.isPotionActive(MobEffects.SPEED);
        }
        return false;
    }

    private void toggleWithMessage(String msg) {
        Command.sendMessage(TextUtil.LIGHT_PURPLE + msg);
    }

    private void clean() {
        this.holdTimer.reset();
        this.delayTimer.reset();
        this.map = null;
        this.speedSlot = -1;
        this.strSlot = -1;
        this.stage = 0;
    }

    /*
    @EventHandler
    private final Listener<EventStopHandActive> handActiveListener = new Listener<>(event -> {
        if(this.stage == 5 || this.stage == 11) {
            event.cancel();
        }
    });
    */
}
