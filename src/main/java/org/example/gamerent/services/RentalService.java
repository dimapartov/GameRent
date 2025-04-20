package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;

import java.util.List;


public interface RentalService {

    RentalViewModel createRequest(RentalRequestInputModel input);

    void cancelRequest(Long rentalId);

    RentalViewModel confirmRental(Long rentalId);

    void initiateReturn(Long rentalId);

    void confirmReturn(Long rentalId);

    List<RentalViewModel> getOwnerRequests();

    List<RentalViewModel> getMyRentals();

    void autoDeclinePending();

}