package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortBackpackPacket {

    private final boolean isBlock;
    private final BlockPos blockPos;
    private final boolean isCurio;

    public SortBackpackPacket(boolean isBlock, BlockPos blockPos, boolean isCurio) {
        this.isBlock = isBlock;
        this.blockPos = blockPos;
        this.isCurio = isCurio;
    }

    public static void encode(SortBackpackPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isBlock);
        buf.writeBlockPos(packet.blockPos != null ? packet.blockPos : BlockPos.ZERO);
        buf.writeBoolean(packet.isCurio);
    }

    public static SortBackpackPacket decode(FriendlyByteBuf buf) {
        boolean isBlock = buf.readBoolean();
        BlockPos pos = buf.readBlockPos();
        boolean isCurio = buf.readBoolean();
        return new SortBackpackPacket(isBlock, isBlock ? pos : null, isCurio);
    }

    public static void handle(SortBackpackPacket packet, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        if (!(player.containerMenu instanceof BackpackMenu menu)) return;

        sortHandler(menu.getHandler());
        menu.broadcastChanges();
    }

    private static void sortHandler(ItemStackHandler handler) {
        // Collect all non-empty stacks
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack s = handler.getStackInSlot(i);
            if (!s.isEmpty()) stacks.add(s.copy());
        }

        // Sort by item registry name then count descending
        stacks.sort(Comparator
                .comparing((ItemStack s) -> s.getItem().toString())
                .thenComparingInt(s -> -s.getCount()));

        // Write back — empty slots first cleared, then filled
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        }
    }
}