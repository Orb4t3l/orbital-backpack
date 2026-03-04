package com.orbital.orbitalbackpack.compat;

import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosCompat {

    public static boolean isLoaded() {
        return ModList.get().isLoaded("curios");
    }

    public static ItemStack getBackStack(Player player) {
        if (!isLoaded()) return ItemStack.EMPTY;
        try {
            return CuriosApi.getCuriosHelper()
                    .getCuriosHandler(player)
                    .map(h -> h.getStacksHandler("back")
                            .map(s -> s.getStacks().getStackInSlot(0))
                            .orElse(ItemStack.EMPTY))
                    .orElse(ItemStack.EMPTY);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public static boolean hasBackpackEquipped(Player player) {
        return getBackStack(player).getItem() instanceof Backpack;
    }

    public static void updateBackStackNBT(Player player, CompoundTag inventoryNBT) {
        if (!isLoaded()) return;
        try {
            CuriosApi.getCuriosHelper()
                    .getCuriosHandler(player)
                    .ifPresent(h -> h.getStacksHandler("back").ifPresent(stacks -> {
                        ItemStack stack = stacks.getStacks().getStackInSlot(0);
                        if (!stack.isEmpty()) {
                            ItemData.set(stack, "inventory", inventoryNBT);
                        }
                    }));
        } catch (Exception ignored) {}
    }
}