package com.example.xchange2.data.model;

public class Product {
    private String id;
    private String ownerId;
    private String ownerName;
    private String title;
    private String description;
    private Category category;
    private Condition condition;
    private ListingType listingType;
    private double price;
    private String imageUrl;
    private String location;
    private long createdAt;
    private boolean isAvailable;

    public Product() {}

    public Product(String id, String ownerId, String ownerName, String title, String description,
                   Category category, Condition condition, ListingType listingType,
                   double price, String imageUrl, String location, long createdAt, boolean isAvailable) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.condition = condition;
        this.listingType = listingType;
        this.price = price;
        this.imageUrl = imageUrl;
        this.location = location;
        this.createdAt = createdAt;
        this.isAvailable = isAvailable;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }

    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) { this.listingType = listingType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public boolean isDonate() {
        return listingType == ListingType.DONATE;
    }
}