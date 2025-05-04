package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public class OfferUpdateInputModel {

    @NotBlank(message = "Введите описание")
    private String description;

    @NotNull(message = "Введите стоимость")
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull(message = "Введите минимальное количество дней для аренды")
    @Min(value = 1, message = "Количество дней должно быть больше или равно 1")
    private Integer minRentalDays;

    @NotNull(message = "Введите максимальное количество дней для аренды")
    @Min(value = 1, message = "Количество дней должно быть больше или равно 1")
    private Integer maxRentalDays;


    public OfferUpdateInputModel() {
    }

    public OfferUpdateInputModel(String description, BigDecimal price, Integer minRentalDays, Integer maxRentalDays) {
        this.description = description;
        this.price = price;
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