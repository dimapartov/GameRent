package org.example.gamerent.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.example.gamerent.models.base.IdCreatedModified;

import java.util.Set;


@Entity
@Table(name = "games")
public class Game extends IdCreatedModified {

    private String name;
    private int releaseYear;
    private Set<Offer> offers;


    protected Game() {}


    @Column(name = "name", unique = true)
    public String getName() {
        return name;
    }

    @Column(name = "release_year")
    public int getReleaseYear() {
        return releaseYear;
    }

    @OneToMany(mappedBy = "game")
    public Set<Offer> getOffers() {
        return offers;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

}