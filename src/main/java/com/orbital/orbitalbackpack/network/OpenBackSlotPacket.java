package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;


public class OpenBackSlotPacket {

    public static void encode(OpenBackSlotPacket packet, FriendlyByteBuf buf) {}

    public static OpenBackSlotPacket decode(FriendlyByteBuf buf) {
        return new OpenBackSlotPacket();
    }

    public static void handle(OpenBackSlotPacket packet, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        if (!CuriosCompat.isLoaded()) return;

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