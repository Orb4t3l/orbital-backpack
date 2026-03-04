package com.orbital.orbitalbackpack.events;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.network.SyncInventoryNBTPacket;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.nbt.CompoundTag;
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
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        Player player = event.player;
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (player.tickCount % 5 != 0) return;

        int inventorySlot = -1;
        ItemStack backpackStack = ItemStack.EMPTY;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof Backpack)) continue;
            if (ItemData.getBoolean(stack, "magnet")) {
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
                    && ItemData.getBoolean(curioStack, "magnet")) {
                backpackStack = curioStack;
                isCurio = true;
            }
        }

        if (backpackStack.isEmpty()) return;
        if (!(backpackStack.getItem() instanceof Backpack backpack)) return;

        BackpackMenu openMenu = serverPlayer.containerMenu instanceof BackpackMenu bm ? bm : null;

        ItemStackHandler handler;
        if (openMenu != null) {
            handler = openMenu.getHandler();
        } else {
            handler = new ItemStackHandler(backpack.getTier().getSlots());
            if (ItemData.has(backpackStack, "inventory")) {
                try {
                    handler.deserializeNBT(player.registryAccess(), ItemData.getCompound(backpackStack, "inventory"));
                } catch (Exception e) {
                    OrbitalBackpack.LOGGER.error("Magnet: failed to read backpack inventory", e);
                    return;
                }
            }
        }

        AABB area = player.getBoundingBox().inflate(6.0);
        List<ItemEntity> nearby = new ArrayList<>(
                player.level().getEntitiesOfClass(ItemEntity.class, area));

        boolean changed = false;
        for (ItemEntity itemEntity : nearby) {
            if (!itemEntity.isAlive() || itemEntity.hasPickUpDelay()) continue;
            ItemStack itemStack = itemEntity.getItem();
            if (itemStack.isEmpty() || itemStack.getItem() instanceof Backpack) continue;

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

        CompoundTag serialized = handler.serializeNBT(player.registryAccess());

        if (openMenu != null) {
            // Menu is open — update item stack and broadcast slot changes
            ItemData.set(backpackStack, "inventory", serialized);
            openMenu.broadcastChanges();
        } else {
            // Menu closed — save to item and sync to client
            ItemData.set(backpackStack, "inventory", serialized);
            int syncSlot = isCurio ? -1 : inventorySlot;
            ModNetwork.CHANNEL.send(
                    new SyncInventoryNBTPacket(syncSlot, serialized),
                    PacketDistributor.PLAYER.with(serverPlayer)
            );
        }
    }

    private static ItemStack insertIntoHandler(ItemStackHandler handler, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack remaining = stack.copy();
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (!inSlot.isEmpty() && itemsMatch(inSlot, remaining)) {
                remaining = handler.insertItem(i, remaining, false);
            }
        }
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            if (handler.getStackInSlot(i).isEmpty()) {
                remaining = handler.insertItem(i, remaining, false);
            }
        }
        return remaining;
    }

    private static boolean itemsMatch(ItemStack a, ItemStack b) {
        if (a.getItem() != b.getItem()) return false;
        CompoundTag at = ItemData.getRawTagCopy(a);
        CompoundTag bt = ItemData.getRawTagCopy(b);
        if (at == null && bt == null) return true;
        if (at == null || bt == null) return false;
        return at.equals(bt);
    }
}