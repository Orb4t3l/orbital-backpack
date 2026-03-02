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
import net.minecraftforge.event.network.CustomPayloadEvent;
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

    public static void encode(SortBackpackPacket packet, FriendlyByteBuf buf) {}


    public static SortBackpackPacket decode(FriendlyByteBuf buf) {
        return new SortBackpackPacket();
    }

    public static void handle(SortBackpackPacket packet, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        if (!(player.containerMenu instanceof BackpackMenu menu)) return;
        menu.sortInventory();
    }
}