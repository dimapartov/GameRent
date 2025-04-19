package org.example.gamerent.web.viewmodels.user_input;

import java.math.BigDecimal;


public class OfferCreationInputModel {

    private String description;
    private BigDecimal price;
    private String photo;
    private String brand;
    private String gameName;
    private Integer minRentalDays;
    private Integer maxRentalDays;


    public OfferCreationInputModel() {
    }

    public OfferCreationInputModel(String description, BigDecimal price, String photo, String brand, String gameName,
                                   Integer minRentalDays, Integer maxRentalDays) {
        this.description = description;
        this.price = price;
        this.photo = photo;
        this.brand = brand;
        this.gameName = gameName;
        this.minRentalDays = minRentalDays;
        this.maxRentalDays = maxRentalDays;
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

    public Integer getMinRentalDays() {
        return minRentalDays;
    }

    public void setMinRentalDays(Integer minRentalDays) {
        this.minRentalDays = minRentalDays;
    }

    public Integer getMaxRentalDays() {
        return maxRentalDays;
    }

    public void setMaxRentalDays(Integer maxRentalDays) {
        this.maxRentalDays = maxRentalDays;
    }

}