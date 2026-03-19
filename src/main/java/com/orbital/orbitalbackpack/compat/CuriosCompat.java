package com.orbital.orbitalbackpack.compat;

import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

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
                Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
                if (optional.isEmpty()) return ItemStack.EMPTY;
                return optional.get().findCurio("back", 0)
                        .map(info -> info.stack())
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
                CuriosApi.getCuriosInventory(player).ifPresent(h -> {
                    h.findCurio("back", 0).ifPresent(info -> {
                        if (!info.stack().isEmpty()) {
                            ItemData.set(info.stack(), "inventory", inventoryNBT);
                        }
                    });
                });
            } catch (Exception ignored) {}
        }
    }
}