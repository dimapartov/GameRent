package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


public class OfferCreationInputModel {

    private MultipartFile offerPhoto;

    @NotBlank(message = "Введите описание. Постарайтесь как можно детальнее описать игру")
    private String description;

    @NotNull(message = "Введите стоимость аренды в сутки")
    @DecimalMin(value = "10.00", message = "Стоимость аренды в сутки должна быть не менее 10 рублей")
    private BigDecimal price;

    @NotBlank(message = "Выберите бренд настольной игры из выпадающего списка")
    private String brand;

    @NotBlank(message = "Введите название настольной игры")
    private String gameName;

    @NotNull(message = "Введите минимальное количество дней аренды")
    @Min(value = 1, message = "Количество дней аренды должно быть больше или равно 1")
    private Integer minRentalDays;

    @NotNull(message = "Введите максимальное количество дней аренды")
    @Min(value = 1, message = "Количество дней аренды должно быть больше или равно 1")
    private Integer maxRentalDays;

    @AssertTrue(message = "Пожалуйста, загрузите фото настольной игры")
    public boolean isPhotoProvided() {
        return offerPhoto != null && !offerPhoto.isEmpty();
    }

    @AssertTrue(message = "Минимальное количество дней аренды не может быть больше или равно максимальному")
    public boolean isValidRentalDaysRange() {
        if (minRentalDays == null || maxRentalDays == null) {
            return true;
        }
        return minRentalDays < maxRentalDays;
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