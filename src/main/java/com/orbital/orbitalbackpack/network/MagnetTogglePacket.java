package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.util.ItemData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MagnetTogglePacket(boolean isMainHand) implements CustomPacketPayload {

    public static final Type<MagnetTogglePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "magnet_toggle"));

    public static final StreamCodec<ByteBuf, MagnetTogglePacket> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(MagnetTogglePacket::new, MagnetTogglePacket::isMainHand);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MagnetTogglePacket packet, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        ItemStack stack = packet.isMainHand() ? player.getMainHandItem() : player.getOffhandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof Backpack)) return;
        boolean current = ItemData.getBoolean(stack, "magnet");
        ItemData.setBoolean(stack, "magnet", !current);
    }
}