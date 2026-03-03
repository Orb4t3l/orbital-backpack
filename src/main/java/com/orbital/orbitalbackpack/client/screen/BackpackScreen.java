package com.orbital.orbitalbackpack.client.screen;

import com.orbital.orbitalbackpack.client.BackpackOpenTracker;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.network.*;
import com.orbital.orbitalbackpack.util.ItemData; // <- helper import
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.PacketDistributor;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private static final ResourceLocation PICKUP_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/pickup_button.png");
    private static final ResourceLocation SORT_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/sort_button.png");
    private static final ResourceLocation DEPOSIT_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/deposit_button.png");
    private static final ResourceLocation WITHDRAW_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/withdraw_button.png");
    private static final ResourceLocation MAGNET_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/magnet_button.png");
    private static final ResourceLocation MAGNET_LOCKED_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/magnet_locked_button.png");

    private final BackpackTier tier;
    private EditBox searchBox;
    private String searchText = "";

    private static final int SLOT_HIDDEN_COLOR = 0xFF3D3D3D;
    private static final int SEARCH_BOX_WIDTH = 60;
    private static final int SEARCH_BOX_HEIGHT = 12;
    private static final int BUTTON_SIZE = 12;
    private static final int BUTTON_GAP = 3;
    private static final int MAGNET_ACTIVE_COLOR = 0x3300AAFF;

    public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.tier = menu.getTier();
        this.imageWidth = 176;
        this.imageHeight = 114 + tier.getRows() * 18;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = tier.getRows() * 18 + 32 - 10;

        if (this.minecraft != null && this.minecraft.player != null) {
            BackpackOpenTracker.setOpen(this.minecraft.player.getUUID());
        }

        int sideX = this.leftPos + this.imageWidth + 6;
        int currentY = this.topPos;

        this.searchBox = new EditBox(this.font, sideX, currentY, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT,
                Component.literal(""));
        this.searchBox.setMaxLength(32);
        this.searchBox.setHint(Component.translatable("gui.orbitalbackpack.search"));
        this.searchBox.setResponder(text -> this.searchText = text.toLowerCase());
        this.addRenderableWidget(this.searchBox);
        currentY += SEARCH_BOX_HEIGHT + BUTTON_GAP;

        if (this.menu.isBlockBased()) {
            TextureButton pickupButton = new TextureButton(
                    sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                    0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 2,
                    PICKUP_BUTTON_TEXTURE, btn -> onPickupClicked());
            pickupButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.pickup")));
            this.addRenderableWidget(pickupButton);
            currentY += BUTTON_SIZE + BUTTON_GAP;
        }

        TextureButton sortButton = new TextureButton(
                sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 2,
                SORT_BUTTON_TEXTURE, btn -> onSortClicked());
        sortButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.sort")));
        this.addRenderableWidget(sortButton);
        currentY += BUTTON_SIZE + BUTTON_GAP;

        TextureButton depositButton = new TextureButton(
                sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 2,
                DEPOSIT_BUTTON_TEXTURE, btn -> onDepositClicked());
        depositButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.deposit")));
        this.addRenderableWidget(depositButton);
        currentY += BUTTON_SIZE + BUTTON_GAP;

        TextureButton withdrawButton = new TextureButton(
                sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 2,
                WITHDRAW_BUTTON_TEXTURE, btn -> onWithdrawClicked());
        withdrawButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.withdraw")));
        this.addRenderableWidget(withdrawButton);
        currentY += BUTTON_SIZE + BUTTON_GAP;

        if (!this.menu.isBlockBased()) {
            boolean unlocked = isMagnetUnlocked();
            ResourceLocation magnetTex = unlocked ? MAGNET_BUTTON_TEXTURE : MAGNET_LOCKED_TEXTURE;
            TextureButton magnetButton = new TextureButton(
                    sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                    0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 2,
                    magnetTex, btn -> { if (unlocked) onMagnetClicked(); });
            magnetButton.setTooltip(Tooltip.create(Component.translatable(
                    unlocked ? "gui.orbitalbackpack.magnet" : "gui.orbitalbackpack.magnet_locked")));
            magnetButton.active = unlocked;
            this.addRenderableWidget(magnetButton);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null) {
            BackpackOpenTracker.setClosed(this.minecraft.player.getUUID());
            this.minecraft.player.playSound(net.minecraft.sounds.SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
        }
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox.isFocused()) {
            if (keyCode == 256) {
                this.searchBox.setFocused(false);
                this.searchBox.setValue("");
                this.searchText = "";
                return true;
            }
            this.searchBox.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (this.searchBox.isFocused()) {
            return this.searchBox.charTyped(c, modifiers);
        }
        return super.charTyped(c, modifiers);
    }

    private ItemStack getBackpackStack() {
        if (this.minecraft == null || this.minecraft.player == null) return ItemStack.EMPTY;
        InteractionHand hand = this.menu.getHand();
        if (hand == null) return ItemStack.EMPTY;
        return this.minecraft.player.getItemInHand(hand);
    }

    private boolean isMagnetUnlocked() {
        ItemStack stack = getBackpackStack();
        // ItemData.getBoolean returns false if no tag exists, so this is safe for empty stacks.
        return ItemData.getBoolean(stack, "magnet_unlocked");
    }

    private boolean isMagnetActive() {
        ItemStack stack = getBackpackStack();
        return ItemData.getBoolean(stack, "magnet");
    }

    private void onPickupClicked() {
        if (this.menu.isBlockBased() && this.menu.getBlockPos() != null) {
            ModNetwork.CHANNEL.send(
                    new PickupBackpackPacket(this.menu.getBlockPos(), tier),
                    PacketDistributor.SERVER.noArg());
        }
    }

    private void onSortClicked() {
        boolean isBlock = this.menu.isBlockBased();
        BlockPos pos = isBlock ? this.menu.getBlockPos() : null;
        boolean isCurio = this.menu.isCurioSlot();
        ModNetwork.CHANNEL.send(
                new SortBackpackPacket(isBlock, pos, isCurio),
                PacketDistributor.SERVER.noArg());
    }

    private void onDepositClicked() {
        boolean isBlock = this.menu.isBlockBased();
        BlockPos pos = isBlock ? this.menu.getBlockPos() : null;
        boolean isMainHand = this.menu.getHand() == InteractionHand.MAIN_HAND;
        ModNetwork.CHANNEL.send(
                new DepositAllPacket(isBlock, pos, isMainHand),
                PacketDistributor.SERVER.noArg());
    }

    private void onWithdrawClicked() {
        boolean isBlock = this.menu.isBlockBased();
        BlockPos pos = isBlock ? this.menu.getBlockPos() : null;
        boolean isMainHand = this.menu.getHand() == InteractionHand.MAIN_HAND;
        ModNetwork.CHANNEL.send(
                new WithdrawAllPacket(isBlock, pos, isMainHand),
                PacketDistributor.SERVER.noArg());
    }

    private void onMagnetClicked() {
        InteractionHand hand = this.menu.getHand();
        if (hand != null) {
            ModNetwork.CHANNEL.send(
                    new MagnetTogglePacket(hand == InteractionHand.MAIN_HAND),
                    PacketDistributor.SERVER.noArg());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (!searchText.isEmpty()) {
            for (Slot slot : this.menu.slots) {
                if (slot instanceof SlotItemHandler) {
                    if (!matchesSearch(slot)) {
                        int slotX = this.leftPos + slot.x;
                        int slotY = this.topPos + slot.y;
                        guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, SLOT_HIDDEN_COLOR);
                    }
                }
            }
        }

        if (!this.menu.isBlockBased() && isMagnetActive()) {
            guiGraphics.fill(
                    this.leftPos - 2, this.topPos - 2,
                    this.leftPos + this.imageWidth + 2, this.topPos + this.imageHeight + 2,
                    MAGNET_ACTIVE_COLOR);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, "Search",
                this.leftPos + this.imageWidth + 6, this.topPos - 9, 0xFFFFFF, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(tier.getTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    private boolean matchesSearch(Slot slot) {
        if (!slot.hasItem()) return false;
        String itemName = slot.getItem().getHoverName().getString().toLowerCase();
        String itemId = slot.getItem().getItem().builtInRegistryHolder().key().location().toString().toLowerCase();
        return itemName.contains(searchText) || itemId.contains(searchText);
    }
}