package com.orbital.orbitalbackpack.client.menu;

import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RestrictedBackpackSlot extends SlotItemHandler {

    public RestrictedBackpackSlot(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !(stack.getItem() instanceof Backpack);
    }
}