package org.example.gamerent.web.viewmodels;

import org.example.gamerent.models.Brand;
import org.example.gamerent.models.consts.OfferStatus;

import java.math.BigDecimal;


public class OfferDemoViewModel {

    private Long id;
    private String photo;
    private String gameName;
    private Brand brand;
    private String description;
    private String owner;
    private BigDecimal price;
    private Integer minRentalDays;
    private Integer maxRentalDays;
    private OfferStatus status;


    public OfferDemoViewModel() {
    }

    public OfferDemoViewModel(Long id, String photo, String gameName, Brand brand, String description, String owner, BigDecimal price, Integer minRentalDays, Integer maxRentalDays, OfferStatus status) {
        this.id = id;
        this.photo = photo;
        this.gameName = gameName;
        this.brand = brand;
        this.description = description;
        this.owner = owner;
        this.price = price;
        this.minRentalDays = minRentalDays;
        this.maxRentalDays = maxRentalDays;
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

}