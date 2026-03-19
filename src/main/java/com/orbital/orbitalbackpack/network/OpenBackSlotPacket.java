package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModMenus;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenBackSlotPacket() implements CustomPacketPayload {

    public static final Type<OpenBackSlotPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "open_back_slot"));

    public static final StreamCodec<ByteBuf, OpenBackSlotPacket> STREAM_CODEC =
            StreamCodec.unit(new OpenBackSlotPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenBackSlotPacket packet, IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();

        ItemStack stack = CuriosCompat.getBackStack(player);
        if (stack.isEmpty() || !(stack.getItem() instanceof Backpack backpack)) return;

        var tier = backpack.getTier();
        var menuType = ModMenus.MENUS.get(tier).get();
        ItemStack curioStack = stack;

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BackpackMenu(menuType, id, inv, tier, curioStack),
                Component.empty()
        ), buf -> {
            buf.writeBoolean(false); // isBlock
            buf.writeBoolean(true);  // isCurio
            buf.writeBoolean(false);
        });
    }
}