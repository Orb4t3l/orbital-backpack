package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OrbitalBackpack.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(0, PickupBackpackPacket.class,
                PickupBackpackPacket::encode,
                PickupBackpackPacket::decode,
                PickupBackpackPacket::handle);
    }
}