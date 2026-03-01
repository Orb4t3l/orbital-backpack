package com.orbital.orbitalbackpack.events;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.capability.BackSlotCapabilityProvider;
import com.orbital.orbitalbackpack.events.MagnetEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BackSlotEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        var provider = new BackSlotCapabilityProvider();
        event.addCapability(
                new ResourceLocation(OrbitalBackpack.MODID, "back_slot"),
                provider
        );
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() && !event.getEntity().level().isClientSide) return;
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(BackSlotCapabilityProvider.BACK_SLOT).ifPresent(oldCap -> {
            event.getEntity().getCapability(BackSlotCapabilityProvider.BACK_SLOT).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    }
}