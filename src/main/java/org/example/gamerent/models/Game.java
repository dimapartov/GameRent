package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;

import java.util.Set;


@Entity
@Table(name = "games")
public class Game extends IdCreatedModified {

    private String name;
    private String description;
    private Brand brand;
    private Set<Offer> offers;

    protected Game() {
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    public Brand getBrand() {
        return brand;
    }

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    public Set<Offer> getOffers() {
        return offers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

}