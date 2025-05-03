package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public class OfferUpdateInputModel {

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Integer minRentalDays;

    @NotNull
    @Min(1)
    private Integer maxRentalDays;


    public OfferUpdateInputModel() {
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

}