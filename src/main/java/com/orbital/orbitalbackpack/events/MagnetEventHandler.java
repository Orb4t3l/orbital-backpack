package com.orbital.orbitalbackpack.events;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.network.SyncInventoryNBTPacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (player.tickCount % 5 != 0) return;

        int inventorySlot = -1;
        ItemStack backpackStack = ItemStack.EMPTY;

        // Find backpack with magnet enabled
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof Backpack)) continue;

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

        BackpackMenu openMenu = null;
        if (serverPlayer.containerMenu instanceof BackpackMenu bm) {
            openMenu = bm;
        }

        ItemStackHandler handler;
        if (openMenu != null) {
            handler = openMenu.getHandler();
        } else {
            handler = new ItemStackHandler(backpack.getTier().getSlots());
            if (ItemData.has(backpackStack, "inventory")) {
                try {
                    handler.deserializeNBT(ItemData.getCompound(backpackStack, "inventory"));
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

        CompoundTag serialized = handler.serializeNBT();

        if (openMenu != null) {
            ItemData.set(backpackStack, "inventory", serialized);
            openMenu.broadcastChanges();
        } else {
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

        // Fill partial stacks
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (!inSlot.isEmpty() && itemsMatch(inSlot, remaining)) {
                remaining = handler.insertItem(i, remaining, false);
            }
        }

        // Fill empty slots
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

    private static class ItemData {

        public static boolean has(ItemStack stack, String key) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            return data != null && data.getUnsafe().contains(key);
        }

        public static boolean getBoolean(ItemStack stack, String key) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            return data != null && data.getUnsafe().getBoolean(key);
        }

        public static CompoundTag getCompound(ItemStack stack, String key) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data == null) return new CompoundTag();
            return data.getUnsafe().getCompound(key);
        }

        public static void set(ItemStack stack, String key, CompoundTag value) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);

            CompoundTag tag = data != null
                    ? data.copyTag()
                    : new CompoundTag();

            tag.put(key, value);

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        public static void setBoolean(ItemStack stack, String key, boolean value) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);

            CompoundTag tag = data != null
                    ? data.copyTag()
                    : new CompoundTag();

            tag.putBoolean(key, value);

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        public static CompoundTag getRawTagCopy(ItemStack stack) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            return data == null ? null : data.copyTag();
        }
    }
}