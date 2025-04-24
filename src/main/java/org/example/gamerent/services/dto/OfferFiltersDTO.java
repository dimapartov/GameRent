package org.example.gamerent.services.dto;

import java.math.BigDecimal;

public class OfferFiltersDTO {

    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private String brand;
    private Boolean myOffers;
    private String sortBy;


    public OfferFiltersDTO() { }

    public OfferFiltersDTO(BigDecimal priceFrom, BigDecimal priceTo, String brand, Boolean myOffers, String sortBy) {
        this.priceFrom = priceFrom;
        this.priceTo   = priceTo;
        this.brand     = brand;
        this.myOffers  = myOffers;
        this.sortBy    = sortBy;
    }


    public BigDecimal getPriceFrom() { return priceFrom; }
    public void setPriceFrom(BigDecimal priceFrom) { this.priceFrom = priceFrom; }

    public BigDecimal getPriceTo() { return priceTo; }
    public void setPriceTo(BigDecimal priceTo) { this.priceTo = priceTo; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Boolean getMyOffers() { return myOffers; }
    public void setMyOffers(Boolean myOffers) { this.myOffers = myOffers; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
}