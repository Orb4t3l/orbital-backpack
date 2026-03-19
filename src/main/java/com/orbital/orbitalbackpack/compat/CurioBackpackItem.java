package com.orbital.orbitalbackpack.compat;

import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioBackpackItem implements ICurioItem {

    public static final CurioBackpackItem INSTANCE = new CurioBackpackItem();

    public boolean canEquip(ItemStack stack, SlotContext slotContext) {
        return slotContext.identifier().equals("back");
    }

    public boolean canUnequip(ItemStack stack, SlotContext slotContext) {
        return true;
    }
}