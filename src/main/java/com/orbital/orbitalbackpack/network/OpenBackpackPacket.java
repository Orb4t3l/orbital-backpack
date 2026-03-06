package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModMenus;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenBackpackPacket(int slot, boolean isMainHand)
        implements CustomPacketPayload {

    public static final Type<OpenBackpackPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "open_backpack"));

    public static final StreamCodec<ByteBuf, OpenBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, OpenBackpackPacket::slot,
            ByteBufCodecs.BOOL, OpenBackpackPacket::isMainHand,
            OpenBackpackPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenBackpackPacket packet, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        ItemStack stack = player.getInventory().getItem(packet.slot());
        if (stack.isEmpty() || !(stack.getItem() instanceof Backpack backpack)) return;

        var tier = backpack.getTier();
        var menuType = ModMenus.MENUS.get(tier).get();
        InteractionHand hand = packet.isMainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BackpackMenu(menuType, id, inv, hand, tier),
                Component.empty()
        ), buf -> {
            buf.writeBoolean(false);
            buf.writeBoolean(false);
            buf.writeBoolean(packet.isMainHand());
        });
    }
}