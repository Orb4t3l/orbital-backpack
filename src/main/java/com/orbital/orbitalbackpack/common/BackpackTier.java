package com.orbital.orbitalbackpack.common;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public enum BackpackTier implements net.minecraft.util.StringRepresentable {
    LEATHER(3), GOLD(4), IRON(5), DIAMOND(6), NETHERITE(7);

    private final int rows;

    public static final Codec<BackpackTier> CODEC =
            net.minecraft.util.StringRepresentable.fromEnum(BackpackTier::values);

    public static final StreamCodec<ByteBuf, BackpackTier> STREAM_CODEC =
            ByteBufCodecs.INT.map(BackpackTier::fromOrdinal, Enum::ordinal);

    BackpackTier(int rows) { this.rows = rows; }

    @Override
    public String getSerializedName() { return name().toLowerCase(); }  // ← required by interface

    public int getRows() { return rows; }
    public int getSlots() { return rows * 9; }

    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath("orbitalbackpack",
                "textures/gui/" + name().toLowerCase() + "_backpack.png");
    }

    public static BackpackTier fromOrdinal(int ordinal) {
        return values()[Math.max(0, Math.min(ordinal, values().length - 1))];
    }
}