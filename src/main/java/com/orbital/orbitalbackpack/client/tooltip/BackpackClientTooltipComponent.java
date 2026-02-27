package com.orbital.orbitalbackpack.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BackpackClientTooltipComponent implements ClientTooltipComponent {

    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 4;
    private static final int HEADER_HEIGHT = 12;
    private static final int FOOTER_HEIGHT = 12;
    private static final int SLOT_BG_COLOR = 0xFF2D2D2D;
    private static final int SLOT_BORDER_COLOR = 0xFF555555;

    private final List<ItemStack> items;
    private final int totalItems;
    private final int totalSlots;

    public BackpackClientTooltipComponent(BackpackTooltipComponent data) {
        this.items = data.getTopItems();
        this.totalItems = data.getTotalItems();
        this.totalSlots = data.getTotalSlots();
    }

    @Override
    public int getHeight() {
        if (items.isEmpty()) return HEADER_HEIGHT + PADDING;
        return HEADER_HEIGHT + PADDING + SLOT_SIZE + PADDING + FOOTER_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        int slotsToShow = Math.max(1, items.size());
        int slotsWidth = slotsToShow * SLOT_SIZE + (slotsToShow - 1) * 2 + PADDING * 2;
        int headerWidth = font.width("Top Items") + PADDING * 2;
        return Math.max(slotsWidth, headerWidth);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int width = getWidth(font);

        guiGraphics.drawString(font, "Top Items", x + PADDING, y + 2, 0xFFAAAAAA, false);

        int slotStartY = y + HEADER_HEIGHT + PADDING;

        if (items.isEmpty()) {
            guiGraphics.drawString(font, "Empty", x + PADDING, slotStartY, 0xFF888888, false);
            return;
        }

        int totalSlotsWidth = items.size() * SLOT_SIZE + (items.size() - 1) * 2;
        int slotStartX = x + (width - totalSlotsWidth) / 2;

        for (int i = 0; i < items.size(); i++) {
            int slotX = slotStartX + i * (SLOT_SIZE + 2);
            int slotY = slotStartY;

            guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, SLOT_BG_COLOR);
            guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + 1, SLOT_BORDER_COLOR);
            guiGraphics.fill(slotX, slotY + SLOT_SIZE - 1, slotX + SLOT_SIZE, slotY + SLOT_SIZE, SLOT_BORDER_COLOR);
            guiGraphics.fill(slotX, slotY, slotX + 1, slotY + SLOT_SIZE, SLOT_BORDER_COLOR);
            guiGraphics.fill(slotX + SLOT_SIZE - 1, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, SLOT_BORDER_COLOR);

            ItemStack stack = items.get(i);
            guiGraphics.renderItem(stack, slotX + 1, slotY + 1);
            guiGraphics.renderItemDecorations(font, stack, slotX + 1, slotY + 1, null);
        }

        String footerText = totalItems + " / " + totalSlots + " slots used";
        int footerColor = totalItems >= totalSlots ? 0xFFFF5555 : 0xFF888888;
        guiGraphics.drawString(font, footerText, x + PADDING, slotStartY + SLOT_SIZE + PADDING - 2, footerColor, false);
    }
}