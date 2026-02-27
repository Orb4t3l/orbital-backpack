package com.orbital.orbitalbackpack.client.menu;

import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.NetworkHooks;

import java.util.Arrays;
import java.util.Collections;

public class BackpackMenu extends AbstractContainerMenu {


    private static final int rows = 3;
    private ItemStackHandler handler = new ItemStackHandler(rows * 9);
    private ItemStack backpackStack;
    public BackpackMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(ModMenus.BACKPACK.get(), id);
        this.backpackStack = buf.readItem();

        if (backpackStack.hasTag() && backpackStack.getTag().contains("inventory")) {
            handler.deserializeNBT(backpackStack.getTag().getCompound("inventory"));
        }

        addSlots(inv);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide) {
            backpackStack.getOrCreateTag().put("inventory", handler.serializeNBT());
        }
    }

    public BackpackMenu(int id, Inventory inv) {
        super(ModMenus.BACKPACK.get(), id);
        int startX = 8;
        int startY = 18;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(
                        handler,
                        col + row * 9,
                        startX + col * 18,
                        startY + row * 18
                ));
            }

        }
        int playerStartY = startY + 3 * 18 + 14;

// Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        inv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        playerStartY + row * 18
                ));
            }
        }

// Hotbar
        int hotbarY = playerStartY + 58;

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    inv,
                    col,
                    8 + col * 18,
                    hotbarY
            ));
        }


        // TODO: add slots later
    }



    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();

            stack = stackInSlot.copy();

            int containerSlots = 27;

            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }
}

