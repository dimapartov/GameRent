package org.example.gamerent.web.viewmodels;

import java.time.LocalDateTime;


public class ReviewViewModel {

    private Long id;
    private String username;
    private String fullName;
    private Integer rating;
    private String text;
    private LocalDateTime created;


    public ReviewViewModel() {
    }

    public ReviewViewModel(Long id, String username, String fullName, Integer rating, String text, LocalDateTime created) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.rating = rating;
        this.text = text;
        this.created = created;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

}