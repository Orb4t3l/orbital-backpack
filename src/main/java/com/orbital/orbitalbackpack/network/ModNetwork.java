package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar r = event.registrar(OrbitalBackpack.MODID);
        r.playToServer(OpenBackpackPacket.TYPE, OpenBackpackPacket.STREAM_CODEC, OpenBackpackPacket::handle);
        r.playToServer(SortBackpackPacket.TYPE, SortBackpackPacket.STREAM_CODEC, SortBackpackPacket::handle);
        r.playToServer(DepositAllPacket.TYPE, DepositAllPacket.STREAM_CODEC, DepositAllPacket::handle);
        r.playToServer(WithdrawAllPacket.TYPE, WithdrawAllPacket.STREAM_CODEC, WithdrawAllPacket::handle);
        r.playToServer(MagnetTogglePacket.TYPE, MagnetTogglePacket.STREAM_CODEC, MagnetTogglePacket::handle);
        r.playToClient(SyncInventoryNBTPacket.TYPE, SyncInventoryNBTPacket.STREAM_CODEC, SyncInventoryNBTPacket::handle);
        r.playToServer(PickupBackpackPacket.TYPE, PickupBackpackPacket.STREAM_CODEC, PickupBackpackPacket::handle);
    }
}