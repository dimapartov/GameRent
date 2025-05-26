package org.example.gamerent.services.dto;

import org.example.gamerent.models.consts.OfferDifficulty;
import org.example.gamerent.models.consts.OfferGenre;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class OfferFiltersDTO {

    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private String brand;
    private Boolean myOffers;
    private String sortBy;
    private String searchTerm;
    private List<OfferGenre> genres = new ArrayList<>();
    private List<OfferDifficulty> difficulties = new ArrayList<>();

    public OfferFiltersDTO() {
    }

    public OfferFiltersDTO(BigDecimal priceFrom,
                           BigDecimal priceTo,
                           String brand,
                           Boolean myOffers,
                           String sortBy,
                           String searchTerm,
                           List<OfferGenre> genres,
                           List<OfferDifficulty> difficulties) {
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.brand = brand;
        this.myOffers = myOffers;
        this.sortBy = sortBy;
        this.searchTerm = searchTerm;
        this.genres = genres;
        this.difficulties = difficulties;
    }


    public BigDecimal getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(BigDecimal priceFrom) {
        this.priceFrom = priceFrom;
    }

    public BigDecimal getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(BigDecimal priceTo) {
        this.priceTo = priceTo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Boolean getMyOffers() {
        return myOffers;
    }

    public void setMyOffers(Boolean myOffers) {
        this.myOffers = myOffers;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<OfferGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<OfferGenre> genres) {
        this.genres = genres;
    }

    public List<OfferDifficulty> getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(List<OfferDifficulty> difficulties) {
        this.difficulties = difficulties;
    }

}