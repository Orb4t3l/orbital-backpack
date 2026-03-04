package com.orbital.orbitalbackpack.items;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.client.tooltip.BackpackTooltipComponent;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.common.ItemValueHelper;
import com.orbital.orbitalbackpack.registries.ModBlocks;
import com.orbital.orbitalbackpack.registries.ModMenus;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Optional;

public class Backpack extends Item implements ICurioItem {

    private final BackpackTier tier;

    public Backpack(BackpackTier tier) {
        super(new Properties().stacksTo(1));
        this.tier = tier;
    }

    public BackpackTier getTier() { return tier; }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(tier.getSlots());
        if (ItemData.has(stack, "inventory")) {
            handler.deserializeNBT(null, ItemData.getCompound(stack, "inventory"));
        }
        int usedSlots = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) usedSlots++;
        }
        boolean expanded = Screen.hasShiftDown();
        return Optional.of(new BackpackTooltipComponent(
                ItemValueHelper.getTopItems(handler, expanded ? 27 : 5),
                usedSlots, tier.getSlots(), expanded
        ));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player != null && player.isCrouching()) {
            BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
            if (level.getBlockState(pos).isAir()) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, ModBlocks.get(tier).get().defaultBlockState());
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof BackpackBlockEntity backpackBE) {
                        ItemStack held = context.getItemInHand();
                        if (ItemData.has(held, "inventory")) {
                            backpackBE.getHandler().deserializeNBT(null, ItemData.getCompound(held, "inventory"));
                        }
                        backpackBE.setChanged();
                    }
                    if (!player.getAbilities().instabuild) context.getItemInHand().shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            var menuType = ModMenus.MENUS.get(tier).get();
            boolean isMainHand = hand == InteractionHand.MAIN_HAND;
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (id, inv, p) -> new BackpackMenu(menuType, id, inv, hand, tier),
                            Component.translatable("item.orbitalbackpack." + tier.name().toLowerCase() + "_backpack")
                    ),
                    (net.minecraft.network.FriendlyByteBuf buf) -> {
                        buf.writeBoolean(false);
                        buf.writeBoolean(false);
                        buf.writeBoolean(isMainHand);
                    }
            );
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    // ICurioItem — only equippable in back slot
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("back");
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0f, 1.0f);
    }
}