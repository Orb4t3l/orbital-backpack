package com.orbital.orbitalbackpack.client;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.capability.BackSlotCapabilityProvider;
import com.orbital.orbitalbackpack.client.screen.BackSlotScreen;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.network.OpenBackSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeybindHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (ClientSetup.OPEN_BACK_SLOT.consumeClick()) {
            mc.setScreen(new BackSlotScreen());
        }

        if (ClientSetup.OPEN_BACK_BACKPACK.consumeClick()) {
            mc.player.getCapability(BackSlotCapabilityProvider.BACK_SLOT).ifPresent(cap -> {
                if (!cap.getBackStack().isEmpty() && cap.getBackStack().getItem() instanceof Backpack) {
                    ModNetwork.CHANNEL.sendToServer(new OpenBackSlotPacket());
                }
            });
        }
    }
}