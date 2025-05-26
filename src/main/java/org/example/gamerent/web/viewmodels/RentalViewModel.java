package org.example.gamerent.web.viewmodels;

import java.time.LocalDateTime;


public class RentalViewModel {

    private Long offerId;
    private Long rentalId;
    private String offerName;
    private String renterUsername;
    private Integer days;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private String ownerContact;


    public RentalViewModel() {
    }

    public RentalViewModel(Long rentalId,
                           String offerName,
                           String renterUsername,
                           Integer days,
                           LocalDateTime startDate,
                           LocalDateTime endDate,
                           String status,
                           String ownerContact,
                           Long offerId) {
        this.rentalId = rentalId;
        this.offerName = offerName;
        this.renterUsername = renterUsername;
        this.days = days;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.ownerContact = ownerContact;
        this.offerId = offerId;
    }


    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getRenterUsername() {
        return renterUsername;
    }

    public void setRenterUsername(String renterUsername) {
        this.renterUsername = renterUsername;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

}