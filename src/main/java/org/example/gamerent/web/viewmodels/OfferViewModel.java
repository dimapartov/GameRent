package org.example.gamerent.web.viewmodels;

import org.example.gamerent.models.consts.OfferStatus;

import java.math.BigDecimal;


public class OfferViewModel {

    private Long id;
    private String brandName;
    private String gameName;
    private String description;
    private BigDecimal price;
    private Integer minRentalDays;
    private Integer maxRentalDays;
    private OfferStatus status;
    private String photo;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;
    private String ownerUsername;


    public OfferViewModel() {
    }

    public OfferViewModel(Long id,
                          String brandName,
                          String gameName,
                          String description,
                          BigDecimal price,
                          Integer minRentalDays,
                          Integer maxRentalDays,
                          OfferStatus status,
                          String photo,
                          String ownerFirstName,
                          String ownerLastName,
                          String ownerEmail,
                          String ownerUsername) {
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
        this.ownerUsername = ownerUsername;
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

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

}