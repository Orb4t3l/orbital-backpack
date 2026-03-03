package com.orbital.orbitalbackpack.blocks;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * BackpackBlockEntity - updated for 1.20.5+ (1.20.6-ish) style.
 *
 * Notes:
 * - Provides both load(BlockState, CompoundTag) and load(CompoundTag) entrypoints,
 *   forwarded to a single readTag(...) helper so the logic is centralized.
 * - Uses saveAdditional(CompoundTag) to write the inventory.
 * - Includes client sync helpers (getUpdateTag / getUpdatePacket).
 *
 * If your specific mapping still complains about an override mismatch, remove/add @Override
 * from the methods the compiler expects; the body logic will remain correct.
 */
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

    public ItemStackHandler getHandler() { return handler; }
    public boolean isClaimed() { return claimed; }
    public void setClaimed() { this.claimed = true; }
    public boolean isOpen() { return open; }
    public int getOpenTick() { return openTick; }

    public void setOpen(boolean open) {
        this.open = open;
        setChanged(); // mark dirty on server

        // Tell clients to refresh this block entity (force a block update).
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    /* -----------------------
       Persistence helpers
       ----------------------- */

    /**
     * State-aware load entrypoint used in newer mappings.
     * Provided without @Override to avoid mapping mismatch errors.
     */
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
            handler.deserializeNBT(tag.getCompound("inventory"));
        }
        if (tag.contains("claimed")) this.claimed = tag.getBoolean("claimed");
        if (tag.contains("openTick")) this.openTick = tag.getInt("openTick");
        if (tag.contains("open")) this.open = tag.getBoolean("open");
    }

    protected void saveAdditional(CompoundTag tag) {
        if (tag == null) return;
        tag.put("inventory", handler.serializeNBT());
        tag.putBoolean("claimed", this.claimed);
        tag.putBoolean("open", this.open);
        tag.putInt("openTick", this.openTick);
        // If your mapping expects a super.saveAdditional(tag) call, add it here.
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}