package org.example.gamerent.services.dto;

import org.example.gamerent.models.consts.RentalStatus;

import java.time.LocalDateTime;

public class RentalDTO {
    private Long offerId;
    private Long renterId;
    private LocalDateTime startDate;
    private RentalStatus status;

    // Геттеры и сеттеры
    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}