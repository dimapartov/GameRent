package org.example.gamerent.services;

import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.springframework.data.domain.Page;

import java.util.List;


public interface RentalService {

    void createRentalRequest(RentalRequestInputModel rentalRequestInputModel);

    void cancelRentalRequest(Long rentalId);

    void rejectRentalRequest(Long rentalId);

    void confirmRentalRequest(Long rentalId);

    void initiateRentalReturn(Long rentalId);

    void confirmRentalReturn(Long rentalId);

    void autoDeclineRentalRequest();

    Page<RentalViewModel> getMyRentalsByStatus(RentalStatus rentalStatus, int pageNumber, int pageSize);

    Page<RentalViewModel> getPendingRequestsForOwner(int pageNumber, int pageSize);

    Page<RentalViewModel> getActiveRentalsForOwner(int pageNumber, int pageSize);

    Page<RentalViewModel> getPendingReturnsForOwner(int pageNumber, int pageSize);

    Page<RentalViewModel> getCompletedRentalsForOwner(int pageNumber, int pageSize);

}