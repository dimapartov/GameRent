package org.example.gamerent.web.viewmodels;

import java.math.BigDecimal;


public class OfferViewModel {

    private Long id;
    private String brandName;
    private String gameName;
    private String description;
    private BigDecimal price;
    private Integer minRentalDays;
    private Integer maxRentalDays;
    private String status;
    private String photo;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;

    public OfferViewModel() {
    }

    public OfferViewModel(Long id, String brandName, String gameName, String description, BigDecimal price, Integer minRentalDays, Integer maxRentalDays, String status, String photo, String ownerFirstName, String ownerLastName, String ownerEmail) {
        this.id = id;
        this.brandName = brandName;
        this.gameName = gameName;
        this.description = description;
        this.price = price;
        this.minRentalDays = minRentalDays;
        this.maxRentalDays = maxRentalDays;
        this.status = status;
        this.photo = photo;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerEmail = ownerEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

}