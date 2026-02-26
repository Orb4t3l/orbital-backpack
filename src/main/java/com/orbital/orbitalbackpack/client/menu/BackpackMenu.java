package com.orbital.orbitalbackpack.client.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class BackpackMenu extends ChestMenu {

    private final int containerRows = 3;

    public BackpackMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, int rows) {
        super(menuType, containerId, playerInventory, container, rows);
    }
}
