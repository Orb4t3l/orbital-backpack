package com.orbital.orbitalbackpack.blocks;

import com.mojang.serialization.MapCodec;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModBlockEntities;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.registries.ModMenus;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BackpackBlock extends BaseEntityBlock {

    private final MapCodec<BackpackBlock> instanceCodec = MapCodec.unit(() -> this);

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return instanceCodec;
    }

    // Use Shapes.create(AABB) to avoid the broken Block.box() reobf mapping
    private static final VoxelShape SHAPE = Shapes.box(0.25, 0.0, 0.25, 0.75, 0.75, 0.75);

    private final BackpackTier tier;

    public BackpackBlock(BackpackTier tier) {
        super(Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(1.5f).noOcclusion());
        this.tier = tier;
    }

    public BackpackTier getTier() { return tier; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BackpackBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.BACKPACK_BE.get(), BackpackBlockEntity::tick);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                            BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BackpackBlockEntity backpackBE) {
                backpackBE.setOpen(true);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                var menuType = ModMenus.MENUS.get(tier).get();
                BlockPos capturedPos = pos;

                serverPlayer.openMenu(
                        new SimpleMenuProvider(
                                (id, inv, p) -> new BackpackMenu(menuType, id, inv, capturedPos, tier,
                                        backpackBE.getHandler()),
                                Component.translatable("item.orbitalbackpack."
                                        + tier.name().toLowerCase() + "_backpack")
                        ),
                        buf -> {
                            buf.writeBoolean(true);
                            buf.writeBlockPos(capturedPos);
                        }
                );
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BackpackBlockEntity backpackBE && !level.isClientSide) {
                if (!backpackBE.isClaimed()) {
                    ItemStack drop = new ItemStack(ModItems.BACKPACKS.get(tier).get());
                    ItemData.set(drop, "inventory", backpackBE.getHandler().serializeNBT(level.registryAccess()));
                    net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}