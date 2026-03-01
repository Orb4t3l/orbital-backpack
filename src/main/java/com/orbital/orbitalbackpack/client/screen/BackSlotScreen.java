package com.orbital.orbitalbackpack.client.screen;

import com.orbital.orbitalbackpack.capability.BackSlotCapabilityProvider;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.network.SetBackSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class BackSlotScreen extends Screen {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 60;
    private static final int SLOT_SIZE = 18;

    private ItemStack currentStack = ItemStack.EMPTY;
    private int slotX;
    private int slotY;

    public BackSlotScreen() {
        super(Component.translatable("gui.orbitalbackpack.back_slot"));
    }

    @Override
    protected void init() {
        super.init();
        slotX = (this.width - SLOT_SIZE) / 2;
        slotY = (this.height - SLOT_SIZE) / 2;

        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.getCapability(BackSlotCapabilityProvider.BACK_SLOT)
                    .ifPresent(cap -> currentStack = cap.getBackStack().copy());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int bgX = (this.width - WIDTH) / 2;
        int bgY = (this.height - HEIGHT) / 2;
        guiGraphics.fill(bgX, bgY, bgX + WIDTH, bgY + HEIGHT, 0xFF2D2D2D);
        guiGraphics.fill(bgX, bgY, bgX + WIDTH, bgY + 1, 0xFF555555);
        guiGraphics.fill(bgX, bgY + HEIGHT - 1, bgX + WIDTH, bgY + HEIGHT, 0xFF555555);
        guiGraphics.fill(bgX, bgY, bgX + 1, bgY + HEIGHT, 0xFF555555);
        guiGraphics.fill(bgX + WIDTH - 1, bgY, bgX + WIDTH, bgY + HEIGHT, 0xFF555555);

        guiGraphics.drawCenteredString(this.font,
                Component.translatable("gui.orbitalbackpack.back_slot"),
                this.width / 2, bgY + 8, 0xFFFFFF);

        guiGraphics.drawCenteredString(this.font,
                Component.translatable("gui.orbitalbackpack.back_slot_hint"),
                this.width / 2, bgY + HEIGHT - 14, 0xAAAAAA);

        guiGraphics.fill(slotX - 1, slotY - 1, slotX + SLOT_SIZE + 1, slotY + SLOT_SIZE + 1, 0xFF555555);
        guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xFF1A1A1A);

        if (!currentStack.isEmpty()) {
            guiGraphics.renderItem(currentStack, slotX + 1, slotY + 1);
            guiGraphics.renderItemDecorations(this.font, currentStack, slotX + 1, slotY + 1, null);
        }

        if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                && mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {
            guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0x80FFFFFF);
            if (!currentStack.isEmpty()) {
                guiGraphics.renderTooltip(this.font, currentStack, mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("gui.orbitalbackpack.back_slot_empty"),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                && mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return true;

            ItemStack carried = mc.player.containerMenu.getCarried();

            if (button == 0) {
                if (!carried.isEmpty()) {
                    if (!(carried.getItem() instanceof Backpack)) return true;

                    ItemStack toEquip = carried.copy();
                    toEquip.setCount(1);
                    ItemStack remainder = carried.copy();
                    remainder.shrink(1);

                    ItemStack old = currentStack.copy();
                    currentStack = toEquip;

                    mc.player.containerMenu.setCarried(old.isEmpty() ? ItemStack.EMPTY : old);
                    if (!remainder.isEmpty()) {
                        mc.player.containerMenu.setCarried(remainder);
                    }
                } else if (!currentStack.isEmpty()) {
                    mc.player.containerMenu.setCarried(currentStack.copy());
                    currentStack = ItemStack.EMPTY;
                }
            } else if (button == 1) {
                if (!currentStack.isEmpty()) {
                    mc.player.getInventory().placeItemBackInInventory(currentStack.copy());
                    currentStack = ItemStack.EMPTY;
                }
            }

            mc.player.getCapability(BackSlotCapabilityProvider.BACK_SLOT)
                    .ifPresent(cap -> cap.setBackStack(currentStack.copy()));

            ModNetwork.CHANNEL.sendToServer(new SetBackSlotPacket(currentStack.copy()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}