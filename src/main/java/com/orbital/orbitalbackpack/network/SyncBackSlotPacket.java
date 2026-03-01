package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.capability.BackSlotCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBackSlotPacket {

    private final CompoundTag tag;

    public SyncBackSlotPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void encode(SyncBackSlotPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.tag);
    }

    public static SyncBackSlotPacket decode(FriendlyByteBuf buf) {
        return new SyncBackSlotPacket(buf.readNbt());
    }

    public static void handle(SyncBackSlotPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    Player player = Minecraft.getInstance().player;
                    if (player == null) return;
                    player.getCapability(BackSlotCapabilityProvider.BACK_SLOT).ifPresent(cap ->
                            cap.deserializeNBT(packet.tag));
                })
        );
        ctx.get().setPacketHandled(true);
    }
}