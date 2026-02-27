package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
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

            if (packet.isBlockBased) {
                var be = player.serverLevel().getBlockEntity(packet.pos);
                if (be instanceof BackpackBlockEntity backpackBE) {
                    ItemSortHelper.sortAndPull(backpackBE.getHandler(), player);
                    backpackBE.setChanged();
                }
            } else {
                InteractionHand hand = packet.isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                ItemStack stack = player.getItemInHand(hand);
                if (stack.isEmpty()) return;

                BackpackTier tier = null;
                for (BackpackTier t : BackpackTier.values()) {
                    if (stack.getItem() == com.orbital.orbitalbackpack.registries.ModItems.BACKPACKS.get(t).get()) {
                        tier = t;
                        break;
                    }
                }
                if (tier == null) return;

                ItemStackHandler handler = new ItemStackHandler(tier.getSlots());
                if (stack.hasTag() && stack.getTag().contains("inventory")) {
                    handler.deserializeNBT(stack.getTag().getCompound("inventory"));
                }

                ItemSortHelper.sortAndPull(handler, player);
                stack.getOrCreateTag().put("inventory", handler.serializeNBT());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}