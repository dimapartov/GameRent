package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;


@Entity
@Table(name = "reviews")
public class Review extends IdCreatedModified {

    private String text;
    private Integer rating;
    private User reviewer;
    private User reviewee;

    protected Review() { }

    @Column(name = "text")
    public String getText() {
        return text;
    }

    @Column(name = "rating")
    public Integer getRating() {
        return rating;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    public User getReviewer() {
        return reviewer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    public User getReviewee() {
        return reviewee;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }
    public void setReviewee(User reviewee) {
        this.reviewee = reviewee;
    }
}