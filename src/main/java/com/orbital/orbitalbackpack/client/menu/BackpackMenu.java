package com.orbital.orbitalbackpack.client.menu;

import com.orbital.orbitalbackpack.registries.ModMenus;
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

public class BackpackMenu extends AbstractContainerMenu {


    private static final int rows = 3;
    private ItemStackHandler handler = new ItemStackHandler(rows * 9);
    private int startX = 8
    private int startY = 18

    public BackpackMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(ModMenus.BACKPACK.get(), id);
        this.handler = new ItemStackHandler(rows * 9);
    }

    public BackpackMenu(int id, Inventory inv) {
        super(ModMenus.BACKPACK.get(), id);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                count += 1;
                this.addSlot(new SlotItemHandler(handler,  row * 9 + col, startX + col * 18, startY + row * 18)); } }


        // TODO: add slots later
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
