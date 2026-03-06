package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;


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
        if (stack.isEmpty() || !(stack.getItem() instanceof Backpack backpack)) return;

        var tier = backpack.getTier();
        var menuType = ModMenus.MENUS.get(tier).get();
        InteractionHand hand = packet.isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        boolean mainHand = packet.isMainHand;

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BackpackMenu(menuType, id, inv, hand, tier),
                Component.empty()
        ), buf -> {
            buf.writeBoolean(false); // isBlock
            buf.writeBoolean(false); // isCurio
            buf.writeBoolean(mainHand);
        });
    }
}