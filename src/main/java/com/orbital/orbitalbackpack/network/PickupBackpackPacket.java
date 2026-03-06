package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.util.ItemData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PickupBackpackPacket(BlockPos pos, BackpackTier tier)
        implements CustomPacketPayload {

    public static final Type<PickupBackpackPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "pickup_backpack"));

    public static final StreamCodec<ByteBuf, PickupBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PickupBackpackPacket::pos,
            ByteBufCodecs.fromCodec(BackpackTier.CODEC), PickupBackpackPacket::tier,
            PickupBackpackPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(PickupBackpackPacket packet, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        var level = player.serverLevel();
        var blockEntity = level.getBlockEntity(packet.pos());
        if (!(blockEntity instanceof BackpackBlockEntity backpackBE)) return;

        ItemStack backpackItem = new ItemStack(ModItems.BACKPACKS.get(packet.tier()).get());
        ItemData.edit(backpackItem, tag -> tag.put("inventory",
                backpackBE.getHandler().serializeNBT(player.registryAccess())));

        backpackBE.setClaimed();
        player.closeContainer();
        level.removeBlock(packet.pos(), false);

        boolean placed = false;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).isEmpty()) {
                player.getInventory().setItem(i, backpackItem);
                placed = true;
                break;
            }
        }
        if (!placed) player.getInventory().add(backpackItem);
    }
}