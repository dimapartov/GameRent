package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;
import org.example.gamerent.models.consts.OfferStatus;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "offers")
public class Offer extends IdCreatedModified {

    private String description;
    private BigDecimal price;
    private OfferStatus status;
    private String photo;   // URL или путь к изображению оффера
    private Game game;
    private User owner;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    public Game getGame() {
        return game;
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

    public void setGame(Game game) {
        this.game = game;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setRentals(Set<Rental> rentals) {
        this.rentals = rentals;
    }

}