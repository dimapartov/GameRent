package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ReviewInputModel {

    @NotNull
    private String revieweeUsername;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String text;


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