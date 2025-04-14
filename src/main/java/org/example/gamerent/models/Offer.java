package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;
import org.example.gamerent.models.consts.OfferStatus;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "offers")
public class Offer extends IdCreatedModified {

    private User owner;
    private Brand brand;
    private String gameName;
    private String description;
    private BigDecimal price;
    private OfferStatus status;
    private String photo;
    private Set<Rental> rentals;


    protected Offer() {
    }


    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    public BigDecimal getPrice() {
        return price;
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

    @Column(name = "game_name")
    public String getGameName() {
        return gameName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    public Brand getBrand() {
        return brand;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    public User getOwner() {
        return owner;
    }

    @OneToMany(mappedBy = "offer", fetch = FetchType.LAZY)
    public Set<Rental> getRentals() {
        return rentals;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setRentals(Set<Rental> rentals) {
        this.rentals = rentals;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

}