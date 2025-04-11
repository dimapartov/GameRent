package org.example.gamerent.services.impl;

import org.example.gamerent.models.Rental;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.services.RentalService;
import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository, ModelMapper modelMapper) {
        this.rentalRepository = rentalRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RentalViewModel createRental(RentalViewModel rentalVM) {
        Rental rental = modelMapper.map(rentalVM, Rental.class);
        rental = rentalRepository.save(rental);
        return modelMapper.map(rental, RentalViewModel.class);
    }

    @Override
    public RentalViewModel getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id).orElse(null);
        return rental != null ? modelMapper.map(rental, RentalViewModel.class) : null;
    }

    @Override
    public List<RentalViewModel> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        List<RentalViewModel> result = new ArrayList<>();
        for (Rental rental : rentals) {
            result.add(modelMapper.map(rental, RentalViewModel.class));
        }
        return result;
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}