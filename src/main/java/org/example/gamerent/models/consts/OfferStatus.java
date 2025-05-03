package org.example.gamerent.models.consts;

public enum OfferStatus {
    AVAILABLE("Доступно"),
    RENTED("В аренде"),
    UNAVAILABLE("Недоступно"),
    BOOKED("Забронировано");


    private final String displayName;


    OfferStatus(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }


    @Override
    public String toString() {
        return displayName;
    }
}