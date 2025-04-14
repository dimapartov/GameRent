package org.example.gamerent.web.viewmodels;

import java.math.BigDecimal;


public class OfferViewModel {

    private Long id;
    private String status;
    private BigDecimal price;
    private Long gameId; // Идентификатор игры, если требуется

    public OfferViewModel() {
    }

    public OfferViewModel(Long id, String status, BigDecimal price, Long gameId) {
        this.id = id;
        this.status = status;
        this.price = price;
        this.gameId = gameId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        return "OfferViewModel{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", price=" + price +
                ", gameId=" + gameId +
                '}';
    }

}