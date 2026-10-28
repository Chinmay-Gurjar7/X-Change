package com.example.xchange2.data.model;

public enum Category {
    ALL("All"),
    CLOTHING("Clothing"),
    BOOKS("Books"),
    ELECTRONICS("Electronics"),
    FURNITURE("Furniture"),
    SHOES("Shoes"),
    ACCESSORIES("Accessories"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromDisplayName(String name) {
        for (Category c : values()) {
            if (c.displayName.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return ALL;
    }
}