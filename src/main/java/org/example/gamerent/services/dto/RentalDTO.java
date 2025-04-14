package org.example.gamerent.services.dto;

import org.example.gamerent.models.consts.RentalStatus;

import java.time.LocalDateTime;


public class RentalDTO {

    private Long offerId;
    private Long renterId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RentalStatus status;


    public RentalDTO() {
    }


    public Long getOfferId() {
        return offerId;
    }

    public Long getRenterId() {
        return renterId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public RentalStatus getStatus() {
        return status;
    }


    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

}