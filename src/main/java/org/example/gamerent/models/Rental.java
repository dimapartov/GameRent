package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;
import org.example.gamerent.models.consts.RentalStatus;

import java.time.LocalDateTime;


@Entity
@Table(name = "rentals")
public class Rental extends IdCreatedModified {

    private Offer offer;
    private User renter;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RentalStatus status;
    private Integer days;


    public Rental() {
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    public Offer getOffer() {
        return offer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id", nullable = false)
    public User getRenter() {
        return renter;
    }

    @Column(name = "start_date")
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @Column(name = "end_date")
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public RentalStatus getStatus() {
        return status;
    }

    @Column(name = "days", nullable = false)
    public Integer getDays() {
        return days;
    }


    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public void setRenter(User renter) {
        this.renter = renter;
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

    public void setDays(Integer days) {
        this.days = days;
    }

}