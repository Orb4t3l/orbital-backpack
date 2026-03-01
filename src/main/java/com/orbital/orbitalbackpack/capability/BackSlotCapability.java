package com.orbital.orbitalbackpack.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class BackSlotCapability {

    private ItemStack backStack = ItemStack.EMPTY;

    public ItemStack getBackStack() {
        return backStack;
    }

    public void setBackStack(ItemStack stack) {
        this.backStack = stack == null ? ItemStack.EMPTY : stack;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (!backStack.isEmpty()) {
            tag.put("backStack", backStack.save(new CompoundTag()));
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("backStack")) {
            backStack = ItemStack.of(tag.getCompound("backStack"));
        } else {
            backStack = ItemStack.EMPTY;
        }
    }
}