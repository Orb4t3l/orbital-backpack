package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
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
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WithdrawAllPacket(boolean isBlockBased, BlockPos pos, boolean isMainHand)
        implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "withdraw_all");
    public static final Type<WithdrawAllPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<ByteBuf, WithdrawAllPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, WithdrawAllPacket::isBlockBased,
            BlockPos.STREAM_CODEC, p -> p.pos != null ? p.pos : BlockPos.ZERO,
            ByteBufCodecs.BOOL, WithdrawAllPacket::isMainHand,
            WithdrawAllPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(WithdrawAllPacket packet, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        if (!(player.containerMenu instanceof BackpackMenu backpackMenu)) return;

        var handler = backpackMenu.getHandler();

        for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
            ItemStack stack = handler.getStackInSlot(backpackSlot);
            if (stack.isEmpty()) continue;
            ItemStack remaining = stack.copy();

            for (int playerSlot = 9; playerSlot < 36 && !remaining.isEmpty(); playerSlot++) {
                ItemStack playerStack = player.getInventory().getItem(playerSlot);
                if (playerStack.isEmpty() || playerStack.getItem() != remaining.getItem()) continue;
                int space = playerStack.getMaxStackSize() - playerStack.getCount();
                if (space <= 0) continue;
                int toMove = Math.min(space, remaining.getCount());
                playerStack.grow(toMove);
                remaining.shrink(toMove);
                player.getInventory().setItem(playerSlot, playerStack);
            }

            for (int playerSlot = 9; playerSlot < 36 && !remaining.isEmpty(); playerSlot++) {
                if (!player.getInventory().getItem(playerSlot).isEmpty()) continue;
                player.getInventory().setItem(playerSlot, remaining.copy());
                remaining = ItemStack.EMPTY;
            }

            for (int playerSlot = 0; playerSlot < 9 && !remaining.isEmpty(); playerSlot++) {
                if (!player.getInventory().getItem(playerSlot).isEmpty()) continue;
                player.getInventory().setItem(playerSlot, remaining.copy());
                remaining = ItemStack.EMPTY;
            }

            handler.setStackInSlot(backpackSlot, remaining.isEmpty() ? ItemStack.EMPTY : remaining);
        }

        if (packet.isBlockBased() && packet.pos() != null && !packet.pos().equals(BlockPos.ZERO)) {
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