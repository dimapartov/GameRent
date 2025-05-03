package org.example.gamerent.services.dto;

import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.models.Offer;
import org.example.gamerent.models.User;

import java.time.LocalDateTime;


public class RentalDTO {

    private Offer offer;
    private User renter;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RentalStatus status;

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public User getRenter() {
        return renter;
    }

    public void setRenter(User renter) {
        this.renter = renter;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

}