package net.highskiesmc.hsskills.api;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum Rank {
    ONE( "group.rank-i", "I", 2),
    TWO("group.rank-ii", "II", 3),
    THREE("group.rank-iii", "III", 4),
    FOUR("group.rank-iv", "IV", 5),
    FIVE("group.rank-v", "V", 6);
    private final String PERMISSION;
    private final String ROMAN_NUMERAL;
    private final int TOKENS;

    Rank(@NonNull String permission, @NonNull String romanNumeral, int tokens) {
        this.PERMISSION = permission;
        this.ROMAN_NUMERAL = romanNumeral;
        this.TOKENS = tokens;
    }

    public int getTokens() {
        return this.TOKENS;
    }

    @NonNull
    public String getPermission() {
        return this.PERMISSION;

    }

    @NonNull
    public String getRomanNumeral() {
        return this.ROMAN_NUMERAL;
    }
}