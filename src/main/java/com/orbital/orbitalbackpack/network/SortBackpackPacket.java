package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.common.ItemSortHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SortBackpackPacket {

    private final boolean isBlockBased;
    private final BlockPos pos;
    private final boolean isMainHand;

    public SortBackpackPacket(boolean isBlockBased, BlockPos pos, boolean isMainHand) {
        this.isBlockBased = isBlockBased;
        this.pos = pos;
        this.isMainHand = isMainHand;
    }

    public static void encode(SortBackpackPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isBlockBased);
        if (packet.isBlockBased) {
            buf.writeBlockPos(packet.pos);
        } else {
            buf.writeBoolean(packet.isMainHand);
        }
    }

    public static SortBackpackPacket decode(FriendlyByteBuf buf) {
        boolean isBlock = buf.readBoolean();
        if (isBlock) {
            return new SortBackpackPacket(true, buf.readBlockPos(), false);
        } else {
            return new SortBackpackPacket(false, null, buf.readBoolean());
        }
    }

    public static void handle(SortBackpackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.containerMenu instanceof BackpackMenu backpackMenu)) return;

            ItemStackHandler handler = backpackMenu.getHandler();

            ItemSortHelper.pullMatchingFromPlayer(handler, player);
            ItemSortHelper.sortInternal(handler);

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