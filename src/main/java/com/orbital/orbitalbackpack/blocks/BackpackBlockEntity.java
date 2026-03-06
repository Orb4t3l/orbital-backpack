package com.orbital.orbitalbackpack.blocks;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;


public class BackpackBlockEntity extends BlockEntity {

    private final ItemStackHandler handler;
    private boolean claimed = false;
    private boolean open = false;
    private int openTick = 0;

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BACKPACK_BE.get(), pos, state);
        BackpackTier tier = state.getBlock() instanceof BackpackBlock bb ? bb.getTier() : BackpackTier.LEATHER;
        this.handler = new ItemStackHandler(tier.getSlots());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BackpackBlockEntity entity) {
        if (entity.open && entity.openTick < 10) {
            entity.openTick++;
        } else if (!entity.open && entity.openTick > 0) {
            entity.openTick--;
        }
    }

    public ItemStackHandler getHandler() {
        return handler;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed() {
        this.claimed = true;
    }

    public boolean isOpen() {
        return open;
    }

    public int getOpenTick() {
        return openTick;
    }

    public void setOpen(boolean open) {
        this.open = open;
        setChanged(); // mark dirty on server

        // Tell clients to refresh this block entity (force a block update).
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void load(BlockState state, CompoundTag tag) {

        readTag(tag);
    }


    public void load(CompoundTag tag) {
        // If your mapping requires calling super.load(tag), add it here.
        readTag(tag);
    }


    private void readTag(CompoundTag tag) {
        if (tag == null) return;
        if (tag.contains("inventory")) {
            tag.put("inventory", handler.serializeNBT(level.registryAccess()));
        }
        if (tag.contains("claimed")) this.claimed = tag.getBoolean("claimed");
        if (tag.contains("openTick")) this.openTick = tag.getInt("openTick");
        if (tag.contains("open")) this.open = tag.getBoolean("open");
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag == null) return;
        handler.deserializeNBT(level.registryAccess(), tag.getCompound("inventory"));
        tag.putBoolean("claimed", this.claimed);
        tag.putBoolean("open", this.open);
        tag.putInt("openTick", this.openTick);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        handler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}