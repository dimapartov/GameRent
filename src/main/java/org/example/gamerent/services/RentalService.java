package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.springframework.data.domain.Page;

import java.util.List;


public interface RentalService {

    void createRentalRequest(RentalRequestInputModel rentalRequestInputModel);

    void cancelRentalRequest(Long id);

    void rejectRentalRequest(Long id);

    void confirmRentalRequest(Long id);

    void initiateRentalReturn(Long id);

    void confirmRentalReturn(Long id);

    void autoDeclineRentalRequest();

    Page<RentalViewModel> getMyRentals(int pageNumber, int pageSize);

    Page<RentalViewModel> getPendingRequestsForOwner(int pageNumber, int pageSize);

    Page<RentalViewModel> getActiveRentalsForOwner(int pageNumber, int pageSize);

    Page<RentalViewModel> getPendingReturnsForOwner(int pageNumber, int pageSize);

}