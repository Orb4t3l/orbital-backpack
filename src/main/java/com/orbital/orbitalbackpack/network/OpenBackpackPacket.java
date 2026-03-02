package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class OpenBackpackPacket {

    private final int slot;
    private final boolean isMainHand;

    public OpenBackpackPacket(int slot, boolean isMainHand) {
        this.slot = slot;
        this.isMainHand = isMainHand;
    }

    public static void encode(OpenBackpackPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slot);
        buf.writeBoolean(packet.isMainHand);
    }

    public static OpenBackpackPacket decode(FriendlyByteBuf buf) {
        return new OpenBackpackPacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(OpenBackpackPacket packet, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;

        ItemStack stack = player.getInventory().getItem(packet.slot);
        if (stack.isEmpty()) return;

        player.openMenu(BackpackMenu.getMenuProvider(stack, packet.slot, false, false, packet.isMainHand),
                buf -> {
                    buf.writeBoolean(false);
                    buf.writeBoolean(false);
                    buf.writeBoolean(packet.isMainHand);
                });
    }
}