package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;

import java.math.BigDecimal;


@Entity
@Table(name = "offers")
public class Offer extends IdCreatedModified {

    private String description;
    private int condition;
    private BigDecimal price;
    private User owner;
    private Game game;


    protected Offer() {}


    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "condition")
    public int getCondition() {
        return condition;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
    public User getOwner() {
        return owner;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
    public Game getGame() {
        return game;
    }

}