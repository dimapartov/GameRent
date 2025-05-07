package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public class OfferUpdateInputModel {

    @NotBlank(message = "Введите описание")
    private String description;

    @NotNull(message = "Введите стоимость")
    @DecimalMin(value = "10.00", message = "Стоимость аренды в сутки должна быть не менее 10 рублей")
    private BigDecimal price;

    @NotNull(message = "Введите минимальное количество дней аренды")
    @Min(value = 1, message = "Количество дней аренды должно быть больше или равно 1")
    private Integer minRentalDays;

    @NotNull(message = "Введите максимальное количество дней аренды")
    @Min(value = 1, message = "Количество дней аренды должно быть больше или равно 1")
    private Integer maxRentalDays;

    @AssertTrue(message = "Минимальное количество дней аренды не может быть больше или равно максимальному")
    public boolean isValidRentalDaysRange() {
        if (minRentalDays == null || maxRentalDays == null) {
            return true;
        }
        return minRentalDays < maxRentalDays;
    }


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