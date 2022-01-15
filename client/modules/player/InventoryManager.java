package me.vaimok.nekoplus.client.modules.player;

import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.InventoryUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public
class InventoryManager extends Module {

    private final Setting < Integer > threshold = register ( new Setting ( "Threshold" , 0 , 0 , 63 ) );
    private final Setting < Integer > replenishments = register ( new Setting ( "RUpdates" , 0 , 0 , 1000 ) );
    private final Setting < Integer > updates = register ( new Setting ( "HBUpdates" , 100 , 0 , 1000 ) );
    private final Setting < Integer > actions = register ( new Setting ( "Actions" , 2 , 1 , 30 ) );
    private final Setting < Boolean > pauseInv = register ( new Setting ( "PauseInv" , true ) );
    private final Setting < Boolean > putBack = register ( new Setting ( "PutBack" , true ) );

    private final Timer timer = new Timer ( );
    private final Timer replenishTimer = new Timer ( );
    private final Queue < InventoryUtil.Task > taskList = new ConcurrentLinkedQueue <> ( );
    private Map < Integer, ItemStack > hotbar = new ConcurrentHashMap <> ( );

    public InventoryManager( ) {
        super ( "InventoryManager" , "Replenishes your hotbar" , Category.PLAYER , false , false , false );
    }

    @Override
    public
    void onUpdate ( ) {

        if ( mc.currentScreen instanceof GuiContainer && ( ! ( mc.currentScreen instanceof GuiInventory ) || pauseInv.getValue ( ) ) ) {
            return;
        }

        if ( timer.passedMs ( updates.getValue ( ) ) ) {
            mapHotbar ( );
        }

        if ( replenishTimer.passedMs ( replenishments.getValue ( ) ) ) {
            for (int i = 0; i < actions.getValue ( ); i++) {
                InventoryUtil.Task task = taskList.poll ( );
                if ( task != null ) {
                    task.run ( );
                }
            }
            replenishTimer.reset ( );
        }
    }

    @Override
    public
    void onDisable ( ) {
        hotbar.clear ( );
    }

    @Override
    public
    void onLogout ( ) {
        onDisable ( );
    }

    private
    void mapHotbar ( ) {
        Map < Integer, ItemStack > map = new ConcurrentHashMap <> ( );
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot ( i );
            map.put ( i , stack );
        }

        if ( hotbar.isEmpty ( ) ) {
            hotbar = map;
            return;
        }

        Map < Integer, Integer > fromTo = new ConcurrentHashMap <> ( );
        for (Map.Entry < Integer, ItemStack > hotbarItem : map.entrySet ( )) {
            ItemStack stack = hotbarItem.getValue ( );
            Integer slotKey = hotbarItem.getKey ( );
            if ( slotKey != null && stack != null && ( stack.isEmpty || stack.getItem ( ) == Items.AIR || ( stack.stackSize <= threshold.getValue ( ) && stack.stackSize < stack.getMaxStackSize ( ) ) ) ) {
                ItemStack previousStack = hotbarItem.getValue ( );
                if ( stack.isEmpty || stack.getItem ( ) != Items.AIR ) {
                    previousStack = hotbar.get ( slotKey );
                }
                if ( previousStack != null && ! previousStack.isEmpty && previousStack.getItem ( ) != Items.AIR ) {
                    int replenishSlot = getReplenishSlot ( previousStack );
                    if ( replenishSlot == - 1 ) {
                        continue;
                    }
                    fromTo.put ( replenishSlot , InventoryUtil.convertHotbarToInv ( slotKey ) );
                }
            }
        }

        if ( ! fromTo.isEmpty ( ) ) {
            for (Map.Entry < Integer, Integer > slotMove : fromTo.entrySet ( )) {
                taskList.add ( new InventoryUtil.Task ( slotMove.getKey ( ) ) );
                taskList.add ( new InventoryUtil.Task ( slotMove.getValue ( ) ) );
                taskList.add ( new InventoryUtil.Task ( slotMove.getKey ( ) ) );
                taskList.add ( new InventoryUtil.Task ( ) );
            }
        }

        hotbar = map;
    }

    private
    int getReplenishSlot ( ItemStack stack ) {
        AtomicInteger slot = new AtomicInteger ( );
        slot.set ( - 1 );
        for (Map.Entry < Integer, ItemStack > entry : InventoryUtil.getInventoryAndHotbarSlots ( ).entrySet ( )) {
            if ( entry.getKey ( ) < 36 ) {
                if ( InventoryUtil.areStacksCompatible ( stack , entry.getValue ( ) ) ) {
                    slot.set ( entry.getKey ( ) );
                    return slot.get ( );
                }
            }
        }
        return slot.get ( );
    }
}
