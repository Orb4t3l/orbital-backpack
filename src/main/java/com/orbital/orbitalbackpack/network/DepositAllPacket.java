package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.Supplier;

public class DepositAllPacket {

    private final boolean isBlockBased;
    private final BlockPos pos;
    private final boolean isMainHand;

    public DepositAllPacket(boolean isBlockBased, BlockPos pos, boolean isMainHand) {
        this.isBlockBased = isBlockBased;
        this.pos = pos;
        this.isMainHand = isMainHand;
    }

    public static void encode(DepositAllPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isBlockBased);
        if (packet.isBlockBased) {
            buf.writeBlockPos(packet.pos);
        } else {
            buf.writeBoolean(packet.isMainHand);
        }
    }

    public static DepositAllPacket decode(FriendlyByteBuf buf) {
        boolean isBlock = buf.readBoolean();
        if (isBlock) {
            return new DepositAllPacket(true, buf.readBlockPos(), false);
        } else {
            return new DepositAllPacket(false, null, buf.readBoolean());
        }
    }

    public static void handle(DepositAllPacket packet, Supplier<CustomPayloadEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.containerMenu instanceof BackpackMenu backpackMenu)) return;

            ItemStackHandler handler = backpackMenu.getHandler();

            for (int playerSlot = 0; playerSlot < player.getInventory().getContainerSize(); playerSlot++) {
                ItemStack playerStack = player.getInventory().getItem(playerSlot);
                if (playerStack.isEmpty()) continue;
                if (playerStack.getItem() instanceof Backpack) continue;

                for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
                    ItemStack backpackStack = handler.getStackInSlot(backpackSlot);
                    if (backpackStack.isEmpty()) continue;
                    if (backpackStack.getItem() != playerStack.getItem()) continue;

                    int space = backpackStack.getMaxStackSize() - backpackStack.getCount();
                    if (space <= 0) continue;

                    int toMove = Math.min(space, playerStack.getCount());
                    ItemStack updatedBackpack = backpackStack.copy();
                    updatedBackpack.setCount(backpackStack.getCount() + toMove);
                    handler.setStackInSlot(backpackSlot, updatedBackpack);

                    playerStack.shrink(toMove);
                    player.getInventory().setItem(playerSlot, playerStack.isEmpty() ? ItemStack.EMPTY : playerStack);
                    if (playerStack.isEmpty()) break;
                }

                playerStack = player.getInventory().getItem(playerSlot);
                if (playerStack.isEmpty()) continue;
                if (playerStack.getItem() instanceof Backpack) continue;

                for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
                    if (!handler.getStackInSlot(backpackSlot).isEmpty()) continue;

                    int toMove = Math.min(playerStack.getMaxStackSize(), playerStack.getCount());
                    ItemStack toDeposit = playerStack.copy();
                    toDeposit.setCount(toMove);
                    handler.setStackInSlot(backpackSlot, toDeposit);

                    playerStack.shrink(toMove);
                    player.getInventory().setItem(playerSlot, playerStack.isEmpty() ? ItemStack.EMPTY : playerStack);
                    if (playerStack.isEmpty()) break;
                }
            }

            if (packet.isBlockBased) {
                var be = player.serverLevel().getBlockEntity(packet.pos);
                if (be instanceof BackpackBlockEntity backpackBE) {
                    backpackBE.setChanged();
                }
            } else {
                InteractionHand hand = packet.isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                ItemStack stack = player.getItemInHand(hand);
                if (!stack.isEmpty()) {
                    stack.getOrCreateTag().put("inventory", handler.serializeNBT());
                }
            }

            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
        });
        ctx.get().setPacketHandled(true);
    }
}