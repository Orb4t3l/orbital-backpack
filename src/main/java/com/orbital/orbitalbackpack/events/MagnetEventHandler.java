package com.orbital.orbitalbackpack.events;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagnetEventHandler {

    private static final double MAGNET_RANGE = 6.0;

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (player.level().getGameTime() % 5 != 0) return;

        ItemStack magnetBackpack = findMagnetBackpack(player);
        if (magnetBackpack == null) return;

        BackpackTierInfo info = getBackpackInfo(magnetBackpack);
        if (info == null) return;

        ItemStackHandler handler = new ItemStackHandler(info.slots());
        if (magnetBackpack.hasTag() && magnetBackpack.getTag().contains("inventory")) {
            handler.deserializeNBT(magnetBackpack.getTag().getCompound("inventory"));
        }

        List<ItemEntity> nearby = player.level().getEntitiesOfClass(
                ItemEntity.class,
                player.getBoundingBox().inflate(MAGNET_RANGE),
                e -> !e.hasPickUpDelay() && !(e.getItem().getItem() instanceof Backpack)
        );

        boolean changed = false;
        for (ItemEntity itemEntity : nearby) {
            ItemStack dropped = itemEntity.getItem().copy();
            ItemStack remainder = tryInsert(handler, dropped);

            if (remainder.getCount() < dropped.getCount()) {
                changed = true;
                if (remainder.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remainder);
                }
            }
        }

        if (changed) {
            magnetBackpack.getOrCreateTag().put("inventory", handler.serializeNBT());
        }
    }

    private static ItemStack tryInsert(ItemStackHandler handler, ItemStack stack) {
        ItemStack remaining = stack.copy();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slotStack = handler.getStackInSlot(i);
            if (slotStack.isEmpty()) continue;
            if (slotStack.getItem() != remaining.getItem()) continue;

            int space = slotStack.getMaxStackSize() - slotStack.getCount();
            if (space <= 0) continue;

            int toMove = Math.min(space, remaining.getCount());
            ItemStack updated = slotStack.copy();
            updated.setCount(slotStack.getCount() + toMove);
            handler.setStackInSlot(i, updated);
            remaining.shrink(toMove);
            if (remaining.isEmpty()) return ItemStack.EMPTY;
        }

        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) continue;
            handler.setStackInSlot(i, remaining.copy());
            return ItemStack.EMPTY;
        }

        return remaining;
    }

    private static ItemStack findMagnetBackpack(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof Backpack
                    && stack.hasTag()
                    && stack.getTag().getBoolean("magnet")) {
                return stack;
            }
        }
        return null;
    }

    private record BackpackTierInfo(int slots) {}

    private static BackpackTierInfo getBackpackInfo(ItemStack stack) {
        if (!(stack.getItem() instanceof Backpack backpack)) return null;
        return new BackpackTierInfo(backpack.getTier().getSlots());
    }
}