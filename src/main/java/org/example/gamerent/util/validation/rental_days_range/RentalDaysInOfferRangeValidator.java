package org.example.gamerent.util.validation.rental_days_range;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RentalDaysInOfferRangeValidator implements ConstraintValidator<RentalDaysInOfferRange, RentalRequestInputModel> {

    private OfferService offerService;


    @Autowired
    public RentalDaysInOfferRangeValidator(OfferService offerService) {
        this.offerService = offerService;
    }


    @Override
    public boolean isValid(RentalRequestInputModel input, ConstraintValidatorContext context) {
        if (input.getOfferId() == null || input.getDays() == null) {
            return true;
        }
        OfferViewModel offerViewModel = offerService.getOfferById(input.getOfferId());
        Integer minRentalDays = offerViewModel.getMinRentalDays();
        Integer maxRentalDays = offerViewModel.getMaxRentalDays();
        if (input.getDays() < minRentalDays || input.getDays() > maxRentalDays) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Количество дней должно быть между " + minRentalDays + " и " + maxRentalDays)
                    .addPropertyNode("days")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}