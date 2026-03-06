package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.util.ItemData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DepositAllPacket(boolean isBlockBased, BlockPos pos, boolean isMainHand)
        implements CustomPacketPayload {

    public static final Type<DepositAllPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "deposit_all"));

    public static final StreamCodec<ByteBuf, DepositAllPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, DepositAllPacket::isBlockBased,
            BlockPos.STREAM_CODEC, p -> p.pos() != null ? p.pos() : BlockPos.ZERO,
            ByteBufCodecs.BOOL, DepositAllPacket::isMainHand,
            DepositAllPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(DepositAllPacket packet, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        if (!(player.containerMenu instanceof BackpackMenu backpackMenu)) return;
        ItemStackHandler handler = backpackMenu.getHandler();

        for (int playerSlot = 0; playerSlot < player.getInventory().getContainerSize(); playerSlot++) {
            ItemStack playerStack = player.getInventory().getItem(playerSlot);
            if (playerStack.isEmpty() || playerStack.getItem() instanceof Backpack) continue;

            for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
                ItemStack backpackStack = handler.getStackInSlot(backpackSlot);
                if (backpackStack.isEmpty() || backpackStack.getItem() != playerStack.getItem()) continue;
                int space = backpackStack.getMaxStackSize() - backpackStack.getCount();
                if (space <= 0) continue;
                int toMove = Math.min(space, playerStack.getCount());
                ItemStack updated = backpackStack.copy();
                updated.setCount(backpackStack.getCount() + toMove);
                handler.setStackInSlot(backpackSlot, updated);
                playerStack.shrink(toMove);
                player.getInventory().setItem(playerSlot, playerStack.isEmpty() ? ItemStack.EMPTY : playerStack);
                if (playerStack.isEmpty()) break;
            }

            playerStack = player.getInventory().getItem(playerSlot);
            if (playerStack.isEmpty() || playerStack.getItem() instanceof Backpack) continue;

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

        if (packet.isBlockBased() && !packet.pos().equals(BlockPos.ZERO)) {
            var be = player.serverLevel().getBlockEntity(packet.pos());
            if (be instanceof BackpackBlockEntity backpackBE) backpackBE.setChanged();
        } else {
            InteractionHand hand = packet.isMainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty())
                ItemData.edit(stack, tag -> tag.put("inventory", handler.serializeNBT(player.registryAccess())));
        }

        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }
}