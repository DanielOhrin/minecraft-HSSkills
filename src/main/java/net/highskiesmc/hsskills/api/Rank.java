package net.highskiesmc.hsskills.api;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum Rank {
    ONE( "group.rank-i", "I"),
    TWO("group.rank-ii", "II"),
    THREE("group.rank-iii", "III"),
    FOUR("group.rank-iv", "IV"),
    FIVE("group.rank-v", "V");
    private final String PERMISSION;
    private final String ROMAN_NUMERAL;

    Rank(@NonNull String permission, @NonNull String romanNumeral) {
        this.PERMISSION = permission;
        this.ROMAN_NUMERAL = romanNumeral;
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