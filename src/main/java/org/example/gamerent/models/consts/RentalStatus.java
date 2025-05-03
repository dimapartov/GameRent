package org.example.gamerent.models.consts;

public enum RentalStatus {
    PENDING_FOR_CONFIRM("Ожидает подтверждения владельцем"),
    ACTIVE("Активна"),
    RETURNED("Возвращена"),
    PENDING_FOR_RETURN("Ожидает подтверждения возврата владельцем"),
    CANCELED_BY_SCHEDULER("Отменена автоматически по истечении 24 часов"),
    CANCELED_BY_RENTER("Отменена арендатором"),
    CANCELED_BY_OWNER("Отклонена владельцем");


    private final String displayName;


    RentalStatus(String displayName) {
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