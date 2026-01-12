package com.techshop.model;

import java.sql.Timestamp;

public class Wishlists {

    private int wishlistId;
    private int userId;
    private int productId;
    private Timestamp addedDate;

    public Wishlists() {
    }

    public Wishlists(int wishlistId, int userId, int productId, Timestamp addedDate) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.productId = productId;
        this.addedDate = addedDate;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Timestamp getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Timestamp addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public String toString() {
        return "Wishlists{" +
                "wishlistId=" + wishlistId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", addedDate=" + addedDate +
                '}';
    }
}
