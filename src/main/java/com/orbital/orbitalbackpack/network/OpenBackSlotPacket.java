package com.orbital.orbitalbackpack.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenBackSlotPacket() implements CustomPacketPayload {

    public static final Type<OpenBackSlotPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "open_back_slot"));

    public static final StreamCodec<ByteBuf, OpenBackSlotPacket> STREAM_CODEC =
            StreamCodec.unit(new OpenBackSlotPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenBackSlotPacket packet, IPayloadContext ctx) {
        // Curios removed — stub for now
    }
}