package com.orbital.orbitalbackpack.compat;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioBackpackItem implements ICurioItem {

    public static final CurioBackpackItem INSTANCE = new CurioBackpackItem();

    @Override
    public boolean canEquip(ItemStack stack, SlotContext slotContext) {
        return slotContext.identifier().equals("back");
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotContext slotContext) {
        return true;
    }

    @Override
    public ICurio.SoundInfo getEquipSound(ItemStack stack, SlotContext slotContext) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0f, 1.0f);
    }
}