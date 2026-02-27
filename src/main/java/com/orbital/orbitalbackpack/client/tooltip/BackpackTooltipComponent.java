package com.orbital.orbitalbackpack.client.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BackpackTooltipComponent implements TooltipComponent {

    private final List<ItemStack> items;
    private final int totalItems;
    private final int totalSlots;

    public BackpackTooltipComponent(List<ItemStack> items, int totalItems, int totalSlots) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalSlots = totalSlots;
    }

    public List<ItemStack> getTopItems() {
        return items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalSlots() {
        return totalSlots;
    }
}