package org.example.gamerent.models.consts;

public enum RentalStatus {
    PENDING_FOR_CONFIRM("Ожидает подтверждения"),
    ACTIVE("Активна"),
    RETURNED("Возвращена"),
    PENDING_FOR_RETURN("Ожидает подтверждения возврата"),
    CANCELED_BY_SCHEDULER("Отменена автоматически"),
    CANCELED_BY_RENTER("Отменена арендатором");


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