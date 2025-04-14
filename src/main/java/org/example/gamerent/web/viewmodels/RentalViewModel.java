package org.example.gamerent.web.viewmodels;

import java.time.LocalDateTime;


public class RentalViewModel {

    private Long id;
    private Long offerId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public RentalViewModel() {
    }

    public RentalViewModel(Long id, Long offerId, String status, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.offerId = offerId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "RentalViewModel{" +
                "id=" + id +
                ", offerId=" + offerId +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

}