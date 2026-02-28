package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

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

    public static void handle(MagnetTogglePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof Backpack) {
                    boolean current = stack.getOrCreateTag().getBoolean("magnet");
                    stack.getOrCreateTag().putBoolean("magnet", !current);
                    return;
                }
            }

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() instanceof Backpack) {
                    boolean current = stack.getOrCreateTag().getBoolean("magnet");
                    stack.getOrCreateTag().putBoolean("magnet", !current);
                    return;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}