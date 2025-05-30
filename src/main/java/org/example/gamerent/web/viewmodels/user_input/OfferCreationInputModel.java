package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.*;
import org.example.gamerent.models.consts.OfferDifficulty;
import org.example.gamerent.models.consts.OfferGenre;
import org.example.gamerent.util.validation.offer_image_file.ImageFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


public class OfferCreationInputModel {

    @ImageFile(message = "Пожалуйста, загрузите фото настольной игры в формате JPEG или PNG")
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

    @NotNull(message = "Выберите жанр игры")
    private OfferGenre genre;

    @NotNull(message = "Выберите сложность игры")
    private OfferDifficulty difficulty;

    @AssertTrue(message = "Минимальное количество дней аренды не может быть больше или равно максимальному")
    public boolean isValidRentalDaysRange() {
        if (minRentalDays == null || maxRentalDays == null) {
            return true;
        }
        return minRentalDays < maxRentalDays;
    }


    public OfferCreationInputModel() {
    }

    public OfferCreationInputModel(MultipartFile offerPhoto,
                                   String description,
                                   BigDecimal price,
                                   String brand,
                                   String gameName,
                                   Integer minRentalDays,
                                   Integer maxRentalDays,
                                   OfferGenre genre,
                                   OfferDifficulty difficulty) {
        this.offerPhoto = offerPhoto;
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.gameName = gameName;
        this.minRentalDays = minRentalDays;
        this.maxRentalDays = maxRentalDays;
        this.genre = genre;
        this.difficulty = difficulty;
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

    public OfferGenre getGenre() {
        return genre;
    }

    public void setGenre(OfferGenre genre) {
        this.genre = genre;
    }

    public OfferDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(OfferDifficulty difficulty) {
        this.difficulty = difficulty;
    }

}