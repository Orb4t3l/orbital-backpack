package com.orbital.orbitalbackpack.compat;

import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class CuriosCompat {

    public static boolean isLoaded() {
        return ModList.get().isLoaded("curios");
    }

    public static ItemStack getBackStack(Player player) {
        if (!isLoaded()) return ItemStack.EMPTY;
        return CuriosHelper.getBackStack(player);
    }

    public static boolean hasBackpackEquipped(Player player) {
        if (!isLoaded()) return false;
        return CuriosHelper.hasBackpackEquipped(player);
    }

    public static void updateBackStackNBT(Player player, CompoundTag inventoryNBT) {
        if (!isLoaded()) return;
        CuriosHelper.updateBackStackNBT(player, inventoryNBT);
    }

    private static class CuriosHelper {
        static ItemStack getBackStack(Player player) {
            try {
                var optional = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player);
                if (optional.isEmpty()) return ItemStack.EMPTY;
                return optional.get().getStacksHandler("back")
                        .map(s -> s.getStacks().getStackInSlot(0))
                        .orElse(ItemStack.EMPTY);
            } catch (Exception e) {
                return ItemStack.EMPTY;
            }
        }

        static boolean hasBackpackEquipped(Player player) {
            return getBackStack(player).getItem() instanceof com.orbital.orbitalbackpack.items.Backpack;
        }

        static void updateBackStackNBT(Player player, CompoundTag inventoryNBT) {
            try {
                top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
                        .ifPresent(h -> h.getStacksHandler("back").ifPresent(stacks -> {
                            ItemStack stack = stacks.getStacks().getStackInSlot(0);
                            if (!stack.isEmpty()) {
                                ItemData.set(stack, "inventory", inventoryNBT);
                            }
                        }));
            } catch (Exception ignored) {}
        }
    }
}