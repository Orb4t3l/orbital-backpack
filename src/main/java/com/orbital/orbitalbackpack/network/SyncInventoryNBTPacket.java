package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncInventoryNBTPacket(int slotIndex, CompoundTag inventoryNBT)
        implements CustomPacketPayload {

    public static final Type<SyncInventoryNBTPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "sync_inventory_nbt"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, SyncInventoryNBTPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SyncInventoryNBTPacket::slotIndex,
                    ByteBufCodecs.COMPOUND_TAG, SyncInventoryNBTPacket::inventoryNBT,
                    SyncInventoryNBTPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SyncInventoryNBTPacket packet, IPayloadContext ctx) {
        Player player = ctx.player();
        if (player == null) return;
        ItemStack stack = player.getInventory().getItem(packet.slotIndex());
        if (!stack.isEmpty()) {
            ItemData.edit(stack, tag -> tag.put("inventory", packet.inventoryNBT()));
        }
    }
}