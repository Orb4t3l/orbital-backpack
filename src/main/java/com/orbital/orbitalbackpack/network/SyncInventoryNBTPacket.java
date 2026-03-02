package com.orbital.orbitalbackpack.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.Supplier;

public class SyncInventoryNBTPacket {

    private final int slotIndex; // -1 means curios back slot
    private final CompoundTag inventoryNBT;

    public SyncInventoryNBTPacket(int slotIndex, CompoundTag inventoryNBT) {
        this.slotIndex = slotIndex;
        this.inventoryNBT = inventoryNBT;
    }

    public static void encode(SyncInventoryNBTPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slotIndex);
        buf.writeNbt(packet.inventoryNBT);
    }

    public static SyncInventoryNBTPacket decode(FriendlyByteBuf buf) {
        return new SyncInventoryNBTPacket(buf.readInt(), buf.readNbt());
    }

    public static void handle(SyncInventoryNBTPacket packet, Supplier<CustomPayloadEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    Player player = Minecraft.getInstance().player;
                    if (player == null) return;

                    if (packet.slotIndex == -1) {
                        // Curios back slot — update via compat
                        com.orbital.orbitalbackpack.compat.CuriosCompat
                                .updateBackStackNBT(player, packet.inventoryNBT);
                    } else {
                        ItemStack stack = player.getInventory().getItem(packet.slotIndex);
                        if (!stack.isEmpty()) {
                            stack.getOrCreateTag().put("inventory", packet.inventoryNBT);
                        }
                    }
                })
        );
        ctx.get().setPacketHandled(true);
    }
}