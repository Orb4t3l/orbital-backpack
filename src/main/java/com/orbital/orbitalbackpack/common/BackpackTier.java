package com.orbital.orbitalbackpack.common;

public enum BackpackTier {
    LEATHER(1, 9),
    GOLD(2, 18),
    IRON(3, 27),
    DIAMOND(4, 36),
    NETHERITE(5, 45);

    private final int rows;
    private final int slots;

    BackpackTier(int rows, int slots) {
        this.rows = rows;
        this.slots = slots;
    }

    public int getRows() { return rows; }
    public int getSlots() { return slots; }

    public static BackpackTier fromOrdinal(int ordinal) {
        return values()[Math.max(0, Math.min(ordinal, values().length - 1))];
    }
}