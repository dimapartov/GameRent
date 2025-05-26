package org.example.gamerent.models.consts;

public enum OfferGenre {
    CLASSIC("Классическая"),
    CARDS("Карточная"),
    COOPERATIVE("Кооператив"),
    DUEL("Противостояние двух игроков"),
    ECONOMIC("Экономическая"),
    ROLEPLAY("Ролевая"),
    DETECTIVE("Детектив"),
    PARTY("Для вечеринок"),
    STRATEGY("Стратегическая");


    private final String displayName;


    OfferGenre(String displayName) {
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