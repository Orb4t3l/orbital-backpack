package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.capability.BackSlotCapabilityProvider;
import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetBackSlotPacket {

    private final ItemStack stack;

    public SetBackSlotPacket(ItemStack stack) {
        this.stack = stack;
    }

    public static void encode(SetBackSlotPacket packet, FriendlyByteBuf buf) {
        buf.writeItem(packet.stack);
    }

    public static SetBackSlotPacket decode(FriendlyByteBuf buf) {
        return new SetBackSlotPacket(buf.readItem());
    }

    public static void handle(SetBackSlotPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!packet.stack.isEmpty() && !(packet.stack.getItem() instanceof Backpack)) return;

            player.getCapability(BackSlotCapabilityProvider.BACK_SLOT).ifPresent(cap -> {
                cap.setBackStack(packet.stack);
                var syncTag = cap.serializeNBT();
                ModNetwork.CHANNEL.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                        new SyncBackSlotPacket(syncTag)
                );
            });
        });
        ctx.get().setPacketHandled(true);
    }
}