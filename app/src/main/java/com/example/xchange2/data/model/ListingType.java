package com.example.xchange2.data.model;

public enum ListingType {
    SELL("Sell"),
    DONATE("Donate");

    private final String displayName;

    ListingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}