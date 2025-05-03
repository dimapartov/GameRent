package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;

import java.util.List;


public interface RentalService {

    void createRentalRequest(RentalRequestInputModel rentalRequestInputModel);

    void cancelRentalRequest(Long id);

    void rejectRentalRequest(Long id);

    void confirmRentalRequest(Long id);

    void initiateRentalReturn(Long id);

    void confirmRentalReturn(Long id);

    void autoDeclineRentalRequest();

    List<RentalViewModel> getMyRentals();

    List<RentalViewModel> getPendingRequestsForOwner();

    List<RentalViewModel> getActiveRentalsForOwner();

    List<RentalViewModel> getPendingReturnsForOwner();

}