package me.vaimok.nekoplus.client.modules.combat;

import me.vaimok.nekoplus.api.event.events.PacketEvent;
import me.vaimok.nekoplus.api.event.events.ProcessRightClickBlockEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.EntityUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.InventoryUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public
class Offhand extends Module {

    private static Offhand instance;
    private final Queue < InventoryUtil.Task > taskList = new ConcurrentLinkedQueue <> ( );
    private final Timer timer = new Timer ( );
    private final Timer secondTimer = new Timer ( );
    public Setting < Mode > mode = register ( new Setting ( "Mode" , Mode.CRYSTALS ) );
    public Setting < Float > health = register ( new Setting ( "Health" , 13.0f , 0.1f , 36.0f , v -> mode.getValue ( ) != Mode.TOTEMS ) );
    public Setting < Boolean > noOffhandGC = register ( new Setting ( "NoMainhandInteract" , false , v -> mode.getValue ( ) == Mode.GAPPLES ) );
    public int totems = 0;
    public int crystals = 0;
    public int gapples = 0;
    public int lastTotemSlot = - 1;
    public int lastGappleSlot = - 1;
    public int lastCrystalSlot = - 1;
    public boolean holdingCrystal = false;
    public boolean holdingTotem = false;
    public boolean holdingGapple = false;
    public boolean didSwitchThisTick = false;
    int updates = 1;
    int actions = 4;
    private boolean swapToTotem = false;
    private boolean second = false;

    public
    Offhand ( ) {
        super ( "Offhand" , "Allows you to switch up your Offhand." , Category.COMBAT , true , false , false );
        instance = this;
    }

    public static
    Offhand getInstance ( ) {
        if ( instance == null ) {
            instance = new Offhand ( );
        }
        return instance;
    }

    public
    void onItemFinish ( ItemStack stack , EntityLivingBase base ) {
        if ( noOffhandGC.getValue ( ) && base.equals ( mc.player ) && stack.getItem ( ) == mc.player.getHeldItemOffhand ( ).getItem ( ) ) {
            secondTimer.reset ( );
            second = true;
        }
    }

    @Override
    public
    void onTick ( ) {
        if ( nullCheck ( ) || updates == 1 ) {
            return;
        }
        doOffhand ( );
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( ProcessRightClickBlockEvent event ) {
        if ( noOffhandGC.getValue ( ) && event.hand == EnumHand.MAIN_HAND && event.stack.getItem ( ) == Items.END_CRYSTAL && mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && mc.objectMouseOver != null && event.pos == mc.objectMouseOver.getBlockPos ( ) ) {
            event.setCanceled ( true );
            mc.player.setActiveHand ( EnumHand.OFF_HAND );
            mc.playerController.processRightClick ( mc.player , mc.world , EnumHand.OFF_HAND );
        }
    }

    @Override
    public
    void onUpdate ( ) {
        if ( noOffhandGC.getValue ( ) && ! ( mc.currentScreen instanceof GuiScreen ) ) {
            if ( timer.passedMs ( 50 ) ) {
                if ( mc.player != null && mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.END_CRYSTAL && Mouse.isButtonDown ( 1 ) ) {
                    mc.player.setActiveHand ( EnumHand.OFF_HAND );
                    mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown ( 1 );
                }
            } else if ( mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.END_CRYSTAL ) {
                mc.gameSettings.keyBindUseItem.pressed = false;
                //mc.player.stopActiveHand();
            }
        }
        if ( nullCheck ( ) || updates == 2 ) {
            return;
        }
        doOffhand ( );
        if ( secondTimer.passedMs ( 50 ) && second ) {
            second = false;
            timer.reset ( );
        }
    }

    @SubscribeEvent
    public
    void onPacketSend ( PacketEvent.Send event ) {
        if ( noOffhandGC.getValue ( ) && ! fullNullCheck ( ) && mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.END_CRYSTAL && mc.gameSettings.keyBindUseItem.isKeyDown ( ) ) {
            if ( event.getPacket ( ) instanceof CPacketPlayerTryUseItemOnBlock ) {
                CPacketPlayerTryUseItemOnBlock packet = event.getPacket ( );
                if ( packet.getHand ( ) == EnumHand.MAIN_HAND ) {
                    /*if (!NekoAura.placedPos.contains(packet.getPos())) {
                        if(timer.passedMs(timeout.getValue())) {
                            mc.player.setActiveHand(EnumHand.OFF_HAND);
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                        }
                        event.setCanceled(true);
                    }*/
                }
            } else if ( event.getPacket ( ) instanceof CPacketPlayerTryUseItem ) {
                CPacketPlayerTryUseItem packet = event.getPacket ( );
                if ( packet.getHand ( ) == EnumHand.OFF_HAND && ! timer.passedMs ( 50 ) ) {
                    event.setCanceled ( true );
                }
            }
        }
    }

    public
    void doOffhand ( ) {

        didSwitchThisTick = false;
        holdingCrystal = mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.END_CRYSTAL;
        holdingTotem = mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.TOTEM_OF_UNDYING;
        holdingGapple = mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE;

        totems = mc.player.inventory.mainInventory.stream ( ).filter ( itemStack -> itemStack.getItem ( ) == Items.TOTEM_OF_UNDYING ).mapToInt ( ItemStack::getCount ).sum ( );
        if ( holdingTotem ) {
            totems += mc.player.inventory.offHandInventory.stream ( ).filter ( itemStack -> itemStack.getItem ( ) == Items.TOTEM_OF_UNDYING ).mapToInt ( ItemStack::getCount ).sum ( );
        }

        crystals = mc.player.inventory.mainInventory.stream ( ).filter ( itemStack -> itemStack.getItem ( ) == Items.END_CRYSTAL ).mapToInt ( ItemStack::getCount ).sum ( );
        if ( holdingCrystal ) {
            crystals += mc.player.inventory.offHandInventory.stream ( ).filter ( itemStack -> itemStack.getItem ( ) == Items.END_CRYSTAL ).mapToInt ( ItemStack::getCount ).sum ( );
        }

        gapples = mc.player.inventory.mainInventory.stream ( ).filter ( itemStack -> itemStack.getItem ( ) == Items.GOLDEN_APPLE ).mapToInt ( ItemStack::getCount ).sum ( );
        if ( holdingGapple ) {
            gapples += mc.player.inventory.offHandInventory.stream ( ).filter ( itemStack -> itemStack.getItem ( ) == Items.GOLDEN_APPLE ).mapToInt ( ItemStack::getCount ).sum ( );
        }

        doSwitch ( );
    }

    public
    void doSwitch ( ) {

        if ( mc.currentScreen instanceof GuiContainer && ! ( mc.currentScreen instanceof GuiInventory ) ) {
            return;
        }

        Item currentOffhandItem = mc.player.getHeldItemOffhand ( ).getItem ( );
        int lastSlot;

        if ( EntityUtil.getHealth ( mc.player , true ) > health.getValue ( ) ) {
            switch (mode.getValue ( )) {
                case TOTEMS:
                    if ( totems > 0 && ! holdingTotem ) {
                        lastTotemSlot = InventoryUtil.findItemInventorySlot ( Items.TOTEM_OF_UNDYING , false );
                        lastSlot = getLastSlot ( currentOffhandItem , lastTotemSlot );
                        putItemInOffhand ( lastTotemSlot , lastSlot );
                    }
                    break;
                case GAPPLES:
                    if ( gapples > 0 && ! holdingGapple ) {
                        lastGappleSlot = InventoryUtil.findItemInventorySlot ( Items.GOLDEN_APPLE , false );
                        lastSlot = getLastSlot ( currentOffhandItem , lastGappleSlot );
                        putItemInOffhand ( lastGappleSlot , lastSlot );
                    }
                    break;
                default:
                    if ( crystals > 0 && ! holdingCrystal ) {
                        lastCrystalSlot = InventoryUtil.findItemInventorySlot ( Items.END_CRYSTAL , false );
                        lastSlot = getLastSlot ( currentOffhandItem , lastCrystalSlot );
                        putItemInOffhand ( lastCrystalSlot , lastSlot );
                    }
            }
        } else {
            if ( totems > 0 && ! holdingTotem ) {
                lastTotemSlot = InventoryUtil.findItemInventorySlot ( Items.TOTEM_OF_UNDYING , false );
                lastSlot = getLastSlot ( currentOffhandItem , lastTotemSlot );
                putItemInOffhand ( lastTotemSlot , lastSlot );
            }
        }

        for (int i = 0; i < actions; i++) {
            InventoryUtil.Task task = taskList.poll ( );
            if ( task != null ) {
                task.run ( );
                if ( task.isSwitching ( ) ) {
                    didSwitchThisTick = true;
                }
            }
        }
    }

    private
    int getLastSlot ( Item item , int slotIn ) {
        if ( item == Items.END_CRYSTAL ) {
            return lastCrystalSlot;
        } else if ( item == Items.GOLDEN_APPLE ) {
            return lastGappleSlot;
        } else if ( item == Items.TOTEM_OF_UNDYING ) {
            return lastTotemSlot;
        } else if ( item == Items.AIR ) {
            return - 1;
        } else {
            return slotIn;
        }
    }

    private
    void putItemInOffhand ( int slotIn , int slotOut ) {
        if ( slotIn != - 1 && taskList.isEmpty ( ) ) {
            taskList.add ( new InventoryUtil.Task ( slotIn ) );
            taskList.add ( new InventoryUtil.Task ( 45 ) );
            taskList.add ( new InventoryUtil.Task ( slotOut ) );
            taskList.add ( new InventoryUtil.Task ( ) );
        }
    }

    private
    boolean isItemInOffhand ( ) {
        switch (mode.getValue ( )) {
            case GAPPLES:
                return mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.GOLDEN_APPLE;
            case CRYSTALS:
                return mc.player.getHeldItemOffhand ( ).getItem ( ) == Items.END_CRYSTAL;
        }
        return false;
    }

    private
    boolean isHeldInMainHand ( ) {
        switch (mode.getValue ( )) {
            case GAPPLES:
                return mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.GOLDEN_APPLE;
            case CRYSTALS:
                return mc.player.getHeldItemMainhand ( ).getItem ( ) == Items.END_CRYSTAL;
        }
        return false;
    }

    private
    boolean shouldTotem ( ) {
        if ( isHeldInMainHand ( ) || isSwapToTotem ( ) ) return true;
        return ( mc.player.getHealth ( ) + mc.player.getAbsorptionAmount ( ) ) <= getHealth ( ) || mc.player.getItemStackFromSlot ( EntityEquipmentSlot.CHEST ).getItem ( ) == Items.ELYTRA || mc.player.fallDistance >= 3;
    }

    private
    boolean isNotEmpty ( BlockPos pos ) {
        return mc.world.getEntitiesWithinAABBExcludingEntity ( null , new AxisAlignedBB ( pos ) ).stream ( ).anyMatch ( e -> e instanceof EntityEnderCrystal );
    }

    private
    float getHealth ( ) {
        return health.getValue ( );
    }

    private
    boolean isCrystalsAABBEmpty ( ) {
        return isNotEmpty ( mc.player.getPosition ( ).add ( 1 , 0 , 0 ) ) || isNotEmpty ( mc.player.getPosition ( ).add ( - 1 , 0 , 0 ) ) || isNotEmpty ( mc.player.getPosition ( ).add ( 0 , 0 , 1 ) ) || isNotEmpty ( mc.player.getPosition ( ).add ( 0 , 0 , - 1 ) ) || isNotEmpty ( mc.player.getPosition ( ) );
    }

    int getStackSize ( ) {
        int size = 0;
        if ( shouldTotem ( ) ) {
            for (int i = 45; i > 0; i--) {
                if ( mc.player.inventory.getStackInSlot ( i ).getItem ( ) == Items.TOTEM_OF_UNDYING ) {
                    size += mc.player.inventory.getStackInSlot ( i ).getCount ( );
                }
            }
        } else {
            for (int i = 45; i > 0; i--) {
                if ( mc.player.inventory.getStackInSlot ( i ).getItem ( ) == ( mode.getValue ( ) == Mode.CRYSTALS ? Items.END_CRYSTAL : Items.GOLDEN_APPLE ) ) {
                    size += mc.player.inventory.getStackInSlot ( i ).getCount ( );
                }
            }
        }
        return size;
    }

    int getSlot ( Mode m ) {
        int slot = - 1;
        for (int i = 45; i > 0; i--) {
            if ( mc.player.inventory.getStackInSlot ( i ).getItem ( ) == ( m == Mode.CRYSTALS ? Items.END_CRYSTAL : Items.GOLDEN_APPLE ) ) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    int getTotemSlot ( ) {
        int totemSlot = - 1;
        for (int i = 45; i > 0; i--) {
            if ( mc.player.inventory.getStackInSlot ( i ).getItem ( ) == Items.TOTEM_OF_UNDYING ) {
                totemSlot = i;
                break;
            }
        }
        return totemSlot;
    }

    public
    void setMode ( Mode mode ) {
        this.mode.setValue ( mode );
    }

    public
    boolean isSwapToTotem ( ) {
        return swapToTotem;
    }

    public
    void setSwapToTotem ( boolean swapToTotem ) {
        this.swapToTotem = swapToTotem;
    }

    public
    enum Mode {
        CRYSTALS,
        GAPPLES,
        TOTEMS
    }
}