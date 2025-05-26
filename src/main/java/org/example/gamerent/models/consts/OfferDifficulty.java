package org.example.gamerent.models.consts;

public enum OfferDifficulty {
    EASY("Легкая"),
    MEDIUM("Средняя"),
    HARD("Сложная");


    private final String displayName;


    OfferDifficulty(String displayName) {
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