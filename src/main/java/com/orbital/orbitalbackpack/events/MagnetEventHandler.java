package com.orbital.orbitalbackpack.events;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagnetEventHandler {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        tickCounter++;
        if (tickCounter % 5 != 0) return;

        // Check player inventory first
        ItemStack magnetBackpack = findMagnetBackpack(player);

        // If not found in inventory, check Curios back slot
        if (magnetBackpack.isEmpty() && CuriosCompat.isLoaded()) {
            ItemStack curioStack = CuriosCompat.getBackStack(player);
            if (!curioStack.isEmpty() && curioStack.getItem() instanceof Backpack
                    && curioStack.hasTag() && curioStack.getTag().getBoolean("magnet")) {
                magnetBackpack = curioStack;
            }
        }

        if (magnetBackpack.isEmpty()) return;

        final ItemStack backpackStack = magnetBackpack;
        ItemStackHandler handler = new ItemStackHandler(
                ((Backpack) backpackStack.getItem()).getTier().getSlots());
        if (backpackStack.hasTag() && backpackStack.getTag().contains("inventory")) {
            handler.deserializeNBT(backpackStack.getTag().getCompound("inventory"));
        }

        AABB area = player.getBoundingBox().inflate(6.0);
        List<ItemEntity> nearbyItems = player.level().getEntitiesOfClass(ItemEntity.class, area);

        boolean changed = false;
        for (ItemEntity itemEntity : nearbyItems) {
            if (itemEntity.hasPickUpDelay()) continue;
            ItemStack itemStack = itemEntity.getItem();
            if (itemStack.getItem() instanceof Backpack) continue;

            if (tryInsert(handler, itemStack)) {
                changed = true;
                if (itemStack.isEmpty()) {
                    itemEntity.discard();
                }
            }
        }

        if (changed) {
            backpackStack.getOrCreateTag().put("inventory", handler.serializeNBT());
        }
    }

    private static ItemStack findMagnetBackpack(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof Backpack
                    && stack.hasTag() && stack.getTag().getBoolean("magnet")) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean tryInsert(ItemStackHandler handler, ItemStack stack) {
        // Fill existing matching stacks first
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack existing = handler.getStackInSlot(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack)) {
                int space = existing.getMaxStackSize() - existing.getCount();
                if (space > 0) {
                    int toInsert = Math.min(space, stack.getCount());
                    existing.grow(toInsert);
                    stack.shrink(toInsert);
                    if (stack.isEmpty()) return true;
                }
            }
        }
        // Then empty slots
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).isEmpty()) {
                handler.setStackInSlot(i, stack.copy());
                stack.setCount(0);
                return true;
            }
        }
        return false;
    }
}