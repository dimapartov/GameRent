package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;
import org.example.gamerent.models.consts.OfferStatus;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "offers")
public class Offer extends IdCreatedModified {

    // Поля
    private User owner;
    private Brand brand;
    private String gameName;
    private String description;
    private BigDecimal price;
    private Integer minRentalDays;
    private Integer maxRentalDays;
    private OfferStatus status;
    private String photo;
    private Set<Rental> rentals;

    // Конструкторы
    protected Offer() {
    }

    // Геттеры с аннотациями
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    public User getOwner() {
        return owner;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    public Brand getBrand() {
        return brand;
    }

    @Column(name = "game_name", nullable = false)
    public String getGameName() {
        return gameName;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    @Column(name = "min_rental_days", nullable = false)
    public Integer getMinRentalDays() {
        return minRentalDays;
    }

    @Column(name = "max_rental_days", nullable = false)
    public Integer getMaxRentalDays() {
        return maxRentalDays;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public OfferStatus getStatus() {
        return status;
    }

    @Column(name = "photo")
    public String getPhoto() {
        return photo;
    }

    @OneToMany(mappedBy = "offer", fetch = FetchType.LAZY)
    public Set<Rental> getRentals() {
        return rentals;
    }

    // Сеттеры
    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setMinRentalDays(Integer minRentalDays) {
        this.minRentalDays = minRentalDays;
    }

    public void setMaxRentalDays(Integer maxRentalDays) {
        this.maxRentalDays = maxRentalDays;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setRentals(Set<Rental> rentals) {
        this.rentals = rentals;
    }

}