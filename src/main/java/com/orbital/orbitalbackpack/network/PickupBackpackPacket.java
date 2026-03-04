package com.orbital.orbitalbackpack.network;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.Supplier;

public class PickupBackpackPacket {

    private final BlockPos pos;
    private final BackpackTier tier;

    public PickupBackpackPacket(BlockPos pos, BackpackTier tier) {
        this.pos = pos;
        this.tier = tier;
    }

    public static void encode(PickupBackpackPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeEnum(packet.tier);
    }

    public static PickupBackpackPacket decode(FriendlyByteBuf buf) {
        return new PickupBackpackPacket(buf.readBlockPos(), buf.readEnum(BackpackTier.class));
    }

    public static void handle(PickupBackpackPacket packet, Supplier<CustomPayloadEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            var level = player.serverLevel();
            var blockEntity = level.getBlockEntity(packet.pos);

            if (!(blockEntity instanceof BackpackBlockEntity backpackBE)) return;
            
            ItemStack backpackItem = new ItemStack(ModItems.BACKPACKS.get(packet.tier).get());
            ItemData.edit(backpackItem, tag -> tag.put("inventory",
                    backpackBE.getHandler().serializeNBT(player.registryAccess())));

            backpackBE.setClaimed();
            player.closeContainer();
            level.removeBlock(packet.pos, false);

            boolean placed = false;
            for (int i = 0; i < 9; i++) {
                if (player.getInventory().getItem(i).isEmpty()) {
                    player.getInventory().setItem(i, backpackItem);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                player.getInventory().add(backpackItem);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}