package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.NotNull;
import org.example.gamerent.util.validation.rental_days_range.RentalDaysInOfferRange;


@RentalDaysInOfferRange
public class RentalRequestInputModel {

    @NotNull
    private Long offerId;

    @NotNull(message = "Введите количество дней аренды")
    private Integer days;


    public RentalRequestInputModel() {
    }

    public RentalRequestInputModel(Long offerId, Integer days) {
        this.offerId = offerId;
        this.days = days;
    }


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