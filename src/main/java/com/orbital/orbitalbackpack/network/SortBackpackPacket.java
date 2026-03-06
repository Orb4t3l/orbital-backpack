package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SortBackpackPacket(boolean isBlock, BlockPos blockPos)
        implements CustomPacketPayload {

    public static final Type<SortBackpackPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "sort_backpack"));

    public static final StreamCodec<ByteBuf, SortBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SortBackpackPacket::isBlock,
            BlockPos.STREAM_CODEC, p -> p.blockPos() != null ? p.blockPos() : BlockPos.ZERO,
            SortBackpackPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SortBackpackPacket packet, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        if (!(player.containerMenu instanceof BackpackMenu menu)) return;
        sortHandler(menu.getHandler());
        menu.broadcastChanges();
    }

    private static void sortHandler(ItemStackHandler handler) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack s = handler.getStackInSlot(i);
            if (!s.isEmpty()) stacks.add(s.copy());
        }
        stacks.sort(Comparator.comparing((ItemStack s) -> s.getItem().toString())
                .thenComparingInt(s -> -s.getCount()));
        for (int i = 0; i < handler.getSlots(); i++)
            handler.setStackInSlot(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
    }
}