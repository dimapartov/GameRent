package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class RentalRequestInputModel {

    @NotNull
    private Long offerId;

    @NotNull
    @Min(1)
    private Integer days;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

}