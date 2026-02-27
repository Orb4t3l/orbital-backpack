package com.orbital.orbitalbackpack.common;

public enum BackpackTier {
    LEATHER(3),
    GOLD(4),
    IRON(5),
    DIAMOND(6),
    NETHERITE(7);

    private final int rows;

    BackpackTier(int rows) {
        this.rows = rows;
    }

    public int getRows() { return rows; }
    public int getSlots() { return rows * 9; }

    public static BackpackTier fromOrdinal(int ordinal) {
        return values()[Math.max(0, Math.min(ordinal, values().length - 1))];
    }
}