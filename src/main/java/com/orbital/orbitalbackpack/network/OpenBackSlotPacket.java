package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenBackSlotPacket {

    public static void encode(OpenBackSlotPacket packet, FriendlyByteBuf buf) {}
    public static OpenBackSlotPacket decode(FriendlyByteBuf buf) { return new OpenBackSlotPacket(); }

    public static void handle(OpenBackSlotPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!CuriosCompat.isLoaded()) return;

            ItemStack backpackStack = CuriosCompat.getBackStack(player);
            if (backpackStack.isEmpty()) return;
            if (!(backpackStack.getItem() instanceof Backpack backpack)) return;

            BackpackTier tier = backpack.getTier();

            NetworkHooks.openScreen(
                    player,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new BackpackMenu(
                                    ModMenus.MENUS.get(tier).get(), id, inv,
                                    InteractionHand.MAIN_HAND, tier),
                            Component.translatable("item.orbitalbackpack."
                                    + tier.name().toLowerCase() + "_backpack")
                    ),
                    buf -> {
                        buf.writeBoolean(false);
                        buf.writeBoolean(true);
                    }
            );
        });
        ctx.get().setPacketHandled(true);
    }
}