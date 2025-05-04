package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ReviewInputModel {

    @NotBlank
    private String revieweeUsername;

    @NotNull(message = "Введите рейтинг")
    @Min(value = 1, message = "Минимальная оценка - 1")
    @Max(value = 5, message = "Максимальная оценка - 5")
    private Integer rating;

    @NotBlank(message = "Введите текст отзыва")
    private String text;


    public ReviewInputModel() {
    }

    public ReviewInputModel(String revieweeUsername, Integer rating, String text) {
        this.revieweeUsername = revieweeUsername;
        this.rating = rating;
        this.text = text;
    }


    public String getRevieweeUsername() {
        return revieweeUsername;
    }

    public void setRevieweeUsername(String revieweeUsername) {
        this.revieweeUsername = revieweeUsername;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}