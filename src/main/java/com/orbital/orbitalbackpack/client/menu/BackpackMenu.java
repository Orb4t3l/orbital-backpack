package com.orbital.orbitalbackpack.client.menu;

import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import net.minecraft.network.FriendlyByteBuf;

public class BackpackMenu extends AbstractContainerMenu {

    public BackpackMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv);
    }

    public BackpackMenu(int id, Inventory inv) {
        super(ModMenus.BACKPACK.get(), id);

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
