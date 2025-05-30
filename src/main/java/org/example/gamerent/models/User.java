package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import java.util.Set;


@Entity
@Table(name = "users")
public class User extends IdCreatedModified {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Set<Offer> offers;
    private Set<Rental> rentals;
    private Set<Review> reviewsGiven; // Отзывы, оставленные пользователем
    private Set<Review> reviewsReceived; // Отзывы, полученные пользователем


    public User() {
    }


    @GenericField
    @Column(name = "username", unique = true, nullable = false)
    public String getUsername() {
        return username;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }

    @Column(name = "is_active", nullable = false)
    public boolean isActive() {
        return isActive;
    }

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    public Set<Offer> getOffers() {
        return offers;
    }

    @OneToMany(mappedBy = "renter", fetch = FetchType.LAZY)
    public Set<Rental> getRentals() {
        return rentals;
    }

    @OneToMany(mappedBy = "reviewer", fetch = FetchType.LAZY)
    public Set<Review> getReviewsGiven() {
        return reviewsGiven;
    }

    @OneToMany(mappedBy = "reviewee", fetch = FetchType.LAZY)
    public Set<Review> getReviewsReceived() {
        return reviewsReceived;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

    public void setRentals(Set<Rental> rentals) {
        this.rentals = rentals;
    }

    public void setReviewsGiven(Set<Review> reviewsGiven) {
        this.reviewsGiven = reviewsGiven;
    }

    public void setReviewsReceived(Set<Review> reviewsReceived) {
        this.reviewsReceived = reviewsReceived;
    }

}