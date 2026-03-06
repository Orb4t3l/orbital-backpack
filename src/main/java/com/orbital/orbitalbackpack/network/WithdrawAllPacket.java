package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.network.CustomPayloadEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

public class WithdrawAllPacket {

    private final boolean isBlockBased;
    private final BlockPos pos;
    private final boolean isMainHand;

    public WithdrawAllPacket(boolean isBlockBased, BlockPos pos, boolean isMainHand) {
        this.isBlockBased = isBlockBased;
        this.pos = pos;
        this.isMainHand = isMainHand;
    }

    public static void encode(WithdrawAllPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isBlockBased);
        if (packet.isBlockBased) {
            buf.writeBlockPos(packet.pos);
        } else {
            buf.writeBoolean(packet.isMainHand);
        }
    }

    public static WithdrawAllPacket decode(FriendlyByteBuf buf) {
        boolean isBlock = buf.readBoolean();
        if (isBlock) {
            return new WithdrawAllPacket(true, buf.readBlockPos(), false);
        } else {
            return new WithdrawAllPacket(false, null, buf.readBoolean());
        }
    }

    public static void handle(WithdrawAllPacket packet, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        if (!(player.containerMenu instanceof BackpackMenu backpackMenu)) return;

        ItemStackHandler handler = backpackMenu.getHandler();

        for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
            ItemStack stack = handler.getStackInSlot(backpackSlot);
            if (stack.isEmpty()) continue;

            ItemStack remaining = stack.copy();

            // Fill existing partial stacks in main inventory
            for (int playerSlot = 9; playerSlot < 36 && !remaining.isEmpty(); playerSlot++) {
                ItemStack playerStack = player.getInventory().getItem(playerSlot);
                if (playerStack.isEmpty()) continue;
                if (playerStack.getItem() != remaining.getItem()) continue;

                int space = playerStack.getMaxStackSize() - playerStack.getCount();
                if (space <= 0) continue;

                int toMove = Math.min(space, remaining.getCount());
                playerStack.grow(toMove);
                remaining.shrink(toMove);
                player.getInventory().setItem(playerSlot, playerStack);
            }

            // Empty slots in main inventory
            if (!remaining.isEmpty()) {
                for (int playerSlot = 9; playerSlot < 36 && !remaining.isEmpty(); playerSlot++) {
                    if (!player.getInventory().getItem(playerSlot).isEmpty()) continue;
                    player.getInventory().setItem(playerSlot, remaining.copy());
                    remaining = ItemStack.EMPTY;
                }
            }

            // Hotbar as fallback
            if (!remaining.isEmpty()) {
                for (int playerSlot = 0; playerSlot < 9 && !remaining.isEmpty(); playerSlot++) {
                    if (!player.getInventory().getItem(playerSlot).isEmpty()) continue;
                    player.getInventory().setItem(playerSlot, remaining.copy());
                    remaining = ItemStack.EMPTY;
                }
            }

            handler.setStackInSlot(backpackSlot, remaining.isEmpty() ? ItemStack.EMPTY : remaining);
        }

        if (packet.isBlockBased && packet.pos != null) {
            var be = player.serverLevel().getBlockEntity(packet.pos);
            if (be instanceof BackpackBlockEntity backpackBE) {
                backpackBE.setChanged();
            }
        } else {
            InteractionHand hand = packet.isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                ItemData.edit(stack, tag -> tag.put("inventory", handler.serializeNBT(player.registryAccess())));
            }
        }

        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }
}