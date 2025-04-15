package org.example.gamerent.web.viewmodels.user_input;

import java.math.BigDecimal;


public class OfferCreationInputModel {

    private String description;
    private BigDecimal price;
    private String photo;
    private String brand;
    private String gameName;


    public OfferCreationInputModel() {
    }

    public OfferCreationInputModel(String description, BigDecimal price, String photo, String brand, String gameName) {
        this.description = description;
        this.price = price;
        this.photo = photo;
        this.brand = brand;
        this.gameName = gameName;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getGameName() {
        return gameName;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

}