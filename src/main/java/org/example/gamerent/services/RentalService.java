package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;

import java.util.List;


public interface RentalService {

    void createRentalRequest(RentalRequestInputModel input);

    void cancelRentalRequest(Long rentalId);

    void rejectRentalRequest(Long rentalId);

    void confirmRentalRequest(Long rentalId);

    void initiateReturn(Long rentalId);

    void confirmReturn(Long rentalId);

    void autoDecline();

    List<RentalViewModel> getOwnerPendingRequests();

    List<RentalViewModel> getMyRentals();

    List<RentalViewModel> getOwnerActiveRentals();

    List<RentalViewModel> getOwnerPendingReturns();

}