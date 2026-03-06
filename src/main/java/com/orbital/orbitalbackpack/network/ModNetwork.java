package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class ModNetwork {

    public static SimpleChannel CHANNEL;

    public static void register() {
        CHANNEL = ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(OrbitalBackpack.MODID, "main"))
                .simpleChannel();

        CHANNEL.messageBuilder(OpenBackpackPacket.class, 0)
                .encoder(OpenBackpackPacket::encode).decoder(OpenBackpackPacket::decode)
                .consumerMainThread(OpenBackpackPacket::handle).add();

        CHANNEL.messageBuilder(SortBackpackPacket.class, 1)
                .encoder(SortBackpackPacket::encode).decoder(SortBackpackPacket::decode)
                .consumerMainThread(SortBackpackPacket::handle).add();

        CHANNEL.messageBuilder(DepositAllPacket.class, 2)
                .encoder(DepositAllPacket::encode).decoder(DepositAllPacket::decode)
                .consumerMainThread(DepositAllPacket::handle).add();

        CHANNEL.messageBuilder(WithdrawAllPacket.class, 3)
                .encoder(WithdrawAllPacket::encode).decoder(WithdrawAllPacket::decode)
                .consumerMainThread(WithdrawAllPacket::handle).add();

        CHANNEL.messageBuilder(MagnetTogglePacket.class, 4)
                .encoder(MagnetTogglePacket::encode).decoder(MagnetTogglePacket::decode)
                .consumerMainThread(MagnetTogglePacket::handle).add();

//        CHANNEL.messageBuilder(OpenBackSlotPacket.class, 5)
//                .encoder(OpenBackSlotPacket::encode).decoder(OpenBackSlotPacket::decode)
//                .consumerMainThread(OpenBackSlotPacket::handle).add();

        CHANNEL.messageBuilder(SyncInventoryNBTPacket.class, 6)
                .encoder(SyncInventoryNBTPacket::encode).decoder(SyncInventoryNBTPacket::decode)
                .consumerMainThread(SyncInventoryNBTPacket::handle).add();
    }
}