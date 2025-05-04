package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


public class OfferCreationInputModel {

    @NotNull(message = "Пожалуйста, загрузите фото")
    private MultipartFile offerPhoto;

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

    @AssertTrue(message = "Пожалуйста, загрузите фото")
    public boolean isPhotoProvided() {
        return offerPhoto != null && !offerPhoto.isEmpty();
    }


    public OfferCreationInputModel() {
    }

    public OfferCreationInputModel(MultipartFile offerPhoto, String description, BigDecimal price, String brand, String gameName, Integer minRentalDays, Integer maxRentalDays) {
        this.offerPhoto = offerPhoto;
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.gameName = gameName;
        this.minRentalDays = minRentalDays;
        this.maxRentalDays = maxRentalDays;
    }


    public MultipartFile getOfferPhoto() {
        return offerPhoto;
    }

    public void setOfferPhoto(MultipartFile offerPhoto) {
        this.offerPhoto = offerPhoto;
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