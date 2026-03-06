package com.orbital.orbitalbackpack.client;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.network.OpenBackSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeybindHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (ClientSetup.OPEN_BACK_BACKPACK.consumeClick()) {
            if (CuriosCompat.isLoaded() && CuriosCompat.hasBackpackEquipped(mc.player)) {
                ModNetwork.CHANNEL.send(new OpenBackSlotPacket(), PacketDistributor.SERVER.noArg());

            }
        }
    }
}