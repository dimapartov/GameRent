package org.example.gamerent.web.viewmodels;

public class ReviewViewModel {

    private Long id;
    private Long reviewerId;
    private Long revieweeId;
    private String text;
    private int rating;

    public ReviewViewModel() {
    }

    public ReviewViewModel(Long id, Long reviewerId, Long revieweeId, String text, int rating) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.text = text;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Long getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "ReviewViewModel{" +
                "id=" + id +
                ", reviewerId=" + reviewerId +
                ", revieweeId=" + revieweeId +
                ", text='" + text + '\'' +
                ", rating=" + rating +
                '}';
    }

}