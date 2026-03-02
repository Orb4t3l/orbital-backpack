package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.Supplier;

public class MagnetTogglePacket {

    private final boolean isMainHand;

    public MagnetTogglePacket(boolean isMainHand) {
        this.isMainHand = isMainHand;
    }

    public static void encode(MagnetTogglePacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isMainHand);
    }

    public static MagnetTogglePacket decode(FriendlyByteBuf buf) {
        return new MagnetTogglePacket(buf.readBoolean());
    }

    public static void handle(MagnetTogglePacket packet, Supplier<CustomPayloadEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            InteractionHand hand = packet.isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);

            if (!(stack.getItem() instanceof Backpack)) return;
            if (!stack.hasTag() || !stack.getTag().getBoolean("magnet_unlocked")) return;

            boolean current = stack.getTag().getBoolean("magnet");
            stack.getTag().putBoolean("magnet", !current);
        });
        ctx.get().setPacketHandled(true);
    }
}