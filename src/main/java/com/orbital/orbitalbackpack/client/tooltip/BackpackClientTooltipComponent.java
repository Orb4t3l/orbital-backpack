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
    private static final int COLUMNS = 9;

    private final List<ItemStack> items;
    private final int totalItems;
    private final int totalSlots;
    private final boolean expanded;

    public BackpackClientTooltipComponent(BackpackTooltipComponent data) {
        this.items = data.getTopItems();
        this.totalItems = data.getTotalItems();
        this.totalSlots = data.getTotalSlots();
        this.expanded = data.isExpanded();
    }

    private int getColumns() {
        if (!expanded) return Math.min(items.size(), 5);
        return items.isEmpty() ? 1 : Math.min(items.size(), COLUMNS);
    }

    private int getRows() {
        if (items.isEmpty()) return 0;
        if (!expanded) return 1;
        return (int) Math.ceil(items.size() / (double) COLUMNS);
    }

    @Override
    public int getHeight() {
        if (items.isEmpty()) return HEADER_HEIGHT + PADDING;
        return HEADER_HEIGHT + PADDING + (getRows() * SLOT_SIZE) + PADDING + FOOTER_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        if (items.isEmpty()) {
            return font.width("Empty Backpack") + PADDING * 2;
        }
        int cols = getColumns();
        int gridWidth = cols * SLOT_SIZE + (cols - 1) * 2 + PADDING * 2;
        String header = expanded ? "Top Items (Shift)" : "Top Items";
        int headerWidth = font.width(header) + PADDING * 2;
        String footer = totalItems + " / " + totalSlots + " slots used";
        int footerWidth = font.width(footer) + PADDING * 2;
        return Math.max(gridWidth, Math.max(headerWidth, footerWidth));
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int width = getWidth(font);

        if (items.isEmpty()) {
            guiGraphics.drawString(font, "Empty Backpack", x + PADDING, y + 2, 0xFF666666, false);
            return;
        }

        String header = expanded ? "Top Items (Shift)" : "Top Items";
        guiGraphics.drawString(font, header, x + PADDING, y + 2, 0xFFAAAAAA, false);

        int slotAreaY = y + HEADER_HEIGHT + PADDING;
        int cols = getColumns();
        int totalGridWidth = cols * SLOT_SIZE + (cols - 1) * 2;
        int slotStartX = x + (width - totalGridWidth) / 2;

        for (int i = 0; i < items.size(); i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int slotX = slotStartX + col * (SLOT_SIZE + 2);
            int slotY = slotAreaY + row * SLOT_SIZE;

            guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, SLOT_BG_COLOR);
            guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + 1, SLOT_BORDER_COLOR);
            guiGraphics.fill(slotX, slotY + SLOT_SIZE - 1, slotX + SLOT_SIZE, slotY + SLOT_SIZE, SLOT_BORDER_COLOR);
            guiGraphics.fill(slotX, slotY, slotX + 1, slotY + SLOT_SIZE, SLOT_BORDER_COLOR);
            guiGraphics.fill(slotX + SLOT_SIZE - 1, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, SLOT_BORDER_COLOR);

            guiGraphics.renderItem(items.get(i), slotX + 1, slotY + 1);
            guiGraphics.renderItemDecorations(font, items.get(i), slotX + 1, slotY + 1, null);
        }

        int footerY = slotAreaY + (getRows() * SLOT_SIZE) + PADDING - 2;
        String footerText = totalItems + " / " + totalSlots + " slots used";
        int footerColor = totalItems >= totalSlots ? 0xFFFF5555 : 0xFF888888;
        guiGraphics.drawString(font, footerText, x + PADDING, footerY, footerColor, false);

        if (!expanded) {
            guiGraphics.drawString(font, "Hold Shift for more", x + PADDING, footerY + 10, 0xFF555555, false);
        }
    }
}