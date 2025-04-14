package org.example.gamerent.services.dto;

public class ReviewDTO {

    private String text;
    private Integer rating;
    private Long reviewerId;
    private Long revieweeId;


    public ReviewDTO() {
    }


    public String getText() {
        return text;
    }

    public Integer getRating() {
        return rating;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public Long getRevieweeId() {
        return revieweeId;
    }


    public void setText(String text) {
        this.text = text;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }

}