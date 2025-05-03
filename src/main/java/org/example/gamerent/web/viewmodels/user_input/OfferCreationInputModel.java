package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public class OfferCreationInputModel {

    private String photo;

    @NotBlank(message = "Введите описание")
    private String description;

    @NotNull(message = "Введите стоимость")
    @DecimalMin(value = "0.01", message = "Цена должна быть не менее 0.01")
    private BigDecimal price;

    @NotBlank(message = "Выберите бренд из выпадающего списка")
    private String brand;

    @NotBlank(message = "Введите название игры")
    private String gameName;

    @NotNull(message = "Введите минимальное количество дней для аренды")
    @Min(value = 1, message = "Количество дней должно быть больше или равно 1")
    private Integer minRentalDays;

    @NotNull(message = "Введите максимальное количество дней для аренды")
    @Min(value = 1, message = "Количество дней должно быть больше или равно 1")
    private Integer maxRentalDays;

    @AssertTrue(message = "Минимальное количество дней не может превышать максимальное")
    public boolean isRentalDaysValid() {
        if (minRentalDays == null || maxRentalDays == null) {
            return true;
        }
        return minRentalDays <= maxRentalDays;
    }


    public OfferCreationInputModel() {
    }

    public OfferCreationInputModel(String description, BigDecimal price, String brand, String gameName, Integer minRentalDays, Integer maxRentalDays, String photo) {
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.gameName = gameName;
        this.minRentalDays = minRentalDays;
        this.maxRentalDays = maxRentalDays;
        this.photo = photo;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        description = d;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal p) {
        price = p;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String b) {
        brand = b;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String g) {
        gameName = g;
    }

    public Integer getMinRentalDays() {
        return minRentalDays;
    }

    public void setMinRentalDays(Integer m) {
        minRentalDays = m;
    }

    public Integer getMaxRentalDays() {
        return maxRentalDays;
    }

    public void setMaxRentalDays(Integer m) {
        maxRentalDays = m;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}