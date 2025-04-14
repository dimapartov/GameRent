package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.RentalViewModel;

import java.util.List;


public interface RentalService {

    RentalViewModel createRental(RentalViewModel rentalVM);

    RentalViewModel getRentalById(Long id);

    List<RentalViewModel> getAllRentals();

    void deleteRental(Long id);

}