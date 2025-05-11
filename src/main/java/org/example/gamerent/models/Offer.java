package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;
import org.example.gamerent.models.consts.OfferStatus;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.math.BigDecimal;
import java.util.Set;


@Indexed
@Entity
@Table(name = "offers")
public class Offer extends IdCreatedModified {

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


    protected Offer() {
    }


    @IndexedEmbedded(includePaths = {"username"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    public User getOwner() {
        return owner;
    }

    @IndexedEmbedded(includePaths = {"name"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    public Brand getBrand() {
        return brand;
    }

    @FullTextField(analyzer = "english")
    @Column(name = "game_name", nullable = false)
    public String getGameName() {
        return gameName;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @GenericField(sortable = Sortable.YES)
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    @GenericField(sortable = Sortable.YES)
    @Column(name = "min_rental_days", nullable = false)
    public Integer getMinRentalDays() {
        return minRentalDays;
    }

    @GenericField(sortable = Sortable.YES)
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