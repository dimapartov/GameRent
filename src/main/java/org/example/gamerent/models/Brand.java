package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;

import java.util.Set;


@Entity
@Table(name = "brands")
public class Brand extends IdCreatedModified {

    private String name;
    private String description;
    private String photo;   // URL или путь к изображению бренда
    private Set<Game> games;

    protected Brand() { }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "photo")
    public String getPhoto() {
        return photo;
    }

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    public Set<Game> getGames() {
        return games;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setGames(Set<Game> games) {
        this.games = games;
    }
}