package com.orbital.orbitalbackpack.compat;

import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CuriosCompat {

    public static boolean isLoaded() {
        return ModList.get().isLoaded("curios");
    }

    public static ItemStack getBackStack(Player player) {
        if (!isLoaded()) return ItemStack.EMPTY;
        try {
            return CuriosApi.getCuriosHelper()
                    .getCuriosHandler(player)
                    .map(handler -> handler.getStacksHandler("back")
                            .map(stacks -> stacks.getStacks().getStackInSlot(0))
                            .orElse(ItemStack.EMPTY))
                    .orElse(ItemStack.EMPTY);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public static boolean hasBackpackEquipped(Player player) {
        ItemStack stack = getBackStack(player);
        return !stack.isEmpty() && stack.getItem() instanceof Backpack;
    }

    public static ICurioItem makeCurioItem(Backpack backpack) {
        return new ICurioItem() {
            @Override
            public boolean canEquip(SlotContext slotContext, ItemStack stack) {
                return slotContext.identifier().equals("back");
            }
        };
    }
}