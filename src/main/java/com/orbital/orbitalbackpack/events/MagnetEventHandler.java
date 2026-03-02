package com.orbital.orbitalbackpack.events;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.network.SyncInventoryNBTPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagnetEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (player.tickCount % 5 != 0) return;

        // Find magnet backpack
        int inventorySlot = -1;
        ItemStack backpackStack = ItemStack.EMPTY;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof Backpack)) continue;
            if (!stack.hasTag()) continue;
            if (stack.getTag().getBoolean("magnet")) {
                backpackStack = stack;
                inventorySlot = i;
                break;
            }
        }

        boolean isCurio = false;
        if (backpackStack.isEmpty() && CuriosCompat.isLoaded()) {
            ItemStack curioStack = CuriosCompat.getBackStack(player);
            if (!curioStack.isEmpty()
                    && curioStack.getItem() instanceof Backpack
                    && curioStack.hasTag()
                    && curioStack.getTag().getBoolean("magnet")) {
                backpackStack = curioStack;
                isCurio = true;
            }
        }

        if (backpackStack.isEmpty()) return;
        if (!(backpackStack.getItem() instanceof Backpack backpack)) return;

        // Check if player currently has a BackpackMenu open
        // If so, use its live handler directly to avoid stale copy overwrite on close
        BackpackMenu openMenu = null;
        if (serverPlayer.containerMenu instanceof BackpackMenu bm) {
            openMenu = bm;
        }

        // Build working handler — either from the open menu or from NBT
        ItemStackHandler handler;
        if (openMenu != null) {
            handler = openMenu.getHandler();
        } else {
            handler = new ItemStackHandler(backpack.getTier().getSlots());
            if (backpackStack.hasTag() && backpackStack.getTag().contains("inventory")) {
                try {
                    handler.deserializeNBT(backpackStack.getTag().getCompound("inventory"));
                } catch (Exception e) {
                    OrbitalBackpack.LOGGER.error("Magnet: failed to read backpack inventory", e);
                    return;
                }
            }
        }

        // Snapshot nearby items
        AABB area = player.getBoundingBox().inflate(6.0);
        List<ItemEntity> nearby = new ArrayList<>(
                player.level().getEntitiesOfClass(ItemEntity.class, area));

        boolean changed = false;
        for (ItemEntity itemEntity : nearby) {
            if (!itemEntity.isAlive()) continue;
            if (itemEntity.hasPickUpDelay()) continue;

            ItemStack itemStack = itemEntity.getItem();
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof Backpack) continue;

            ItemStack before = itemStack.copy();
            ItemStack remaining = insertIntoHandler(handler, itemStack.copy());

            if (remaining.getCount() < before.getCount()) {
                changed = true;
                if (remaining.isEmpty()) {
                    itemEntity.setItem(ItemStack.EMPTY);
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remaining);
                }
                player.take(itemEntity, before.getCount() - remaining.getCount());
            }
        }

        if (!changed) return;

        if (openMenu != null) {
            // Menu is open — push updated handler back to client via broadcastChanges
            // Also write to stack NBT so it saves correctly on close
            backpackStack.getOrCreateTag().put("inventory", handler.serializeNBT());
            openMenu.broadcastChanges();
        } else {
            // Menu is closed — write to stack NBT and send explicit sync packet
            backpackStack.getOrCreateTag().put("inventory", handler.serializeNBT());
            int syncSlot = isCurio ? -1 : inventorySlot;
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncInventoryNBTPacket(syncSlot, handler.serializeNBT())
            );
        }
    }

    private static ItemStack insertIntoHandler(ItemStackHandler handler, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack remaining = stack.copy();

        // Pass 1: fill partial stacks
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameTags(inSlot, remaining)) {
                remaining = handler.insertItem(i, remaining, false);
            }
        }

        // Pass 2: fill empty slots
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            if (handler.getStackInSlot(i).isEmpty()) {
                remaining = handler.insertItem(i, remaining, false);
            }
        }

        return remaining;
    }
}