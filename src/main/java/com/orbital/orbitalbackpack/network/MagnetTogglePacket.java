package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.network.CustomPayloadEvent;

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

    public static void handle(MagnetTogglePacket packet, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;

        ItemStack stack = packet.isMainHand
                ? player.getMainHandItem()
                : player.getOffhandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof Backpack)) return;

        boolean current = ItemData.getBoolean(stack, "magnet");
        ItemData.setBoolean(stack, "magnet", !current);
    }
}