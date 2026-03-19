package com.orbital.orbitalbackpack.client;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.network.OpenBackSlotPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KeybindHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (ClientSetup.OPEN_BACK_BACKPACK.consumeClick()) {
            if (CuriosCompat.isLoaded() && CuriosCompat.hasBackpackEquipped(mc.player)) {
                PacketDistributor.sendToServer(new OpenBackSlotPacket());
            }
        }
    }
}