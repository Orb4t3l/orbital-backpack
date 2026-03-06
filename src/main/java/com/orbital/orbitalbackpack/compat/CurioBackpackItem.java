//package com.orbital.orbitalbackpack.compat;
//
//import net.minecraft.world.item.ItemStack;
//import top.theillusivec4.curios.api.SlotContext;
//import top.theillusivec4.curios.api.type.capability.ICurio;
//import top.theillusivec4.curios.api.type.capability.ICurioItem;
//
//public class CurioBackpackItem implements ICurioItem {
//
//    public static final CurioBackpackItem INSTANCE = new CurioBackpackItem();
//
//    @Override
//    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
//        return slotContext.identifier().equals("back");
//    }
//
//    @Override
//    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
//        return true;
//    }
//
//    @Override
//    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
//        return new ICurio.SoundInfo(
//                net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0f, 1.0f);
//    }
//}