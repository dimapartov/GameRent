package org.example.gamerent.services.impl;

import jakarta.transaction.Transactional;
import org.example.gamerent.models.Offer;
import org.example.gamerent.models.Rental;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.OfferStatus;
import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.RentalService;
import org.example.gamerent.services.dto.RentalDTO;
import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepo;
    private final OfferRepository offerRepo;
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepo,
                             OfferRepository offerRepo,
                             UserRepository userRepo,
                             ModelMapper modelMapper) {
        this.rentalRepo = rentalRepo;
        this.offerRepo = offerRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public void createRentalRequest(RentalRequestInputModel input) {
        Offer offer = offerRepo.findById(input.getOfferId())
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        if (offer.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Offer not available");
        }
        if (input.getDays() < offer.getMinRentalDays() || input.getDays() > offer.getMaxRentalDays()) {
            throw new RuntimeException("Invalid rental days range");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User renter = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setOffer(offer);
        rentalDTO.setRenter(renter);
        rentalDTO.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        rentalDTO.setStartDate(null);
        rentalDTO.setEndDate(LocalDateTime.now().plusDays(input.getDays()));

        Rental rental = modelMapper.map(rentalDTO, Rental.class);
        rental = rentalRepo.save(rental);

        offer.setStatus(OfferStatus.BOOKED);
        offerRepo.save(offer);

        modelMapper.map(rental, RentalViewModel.class);
    }

    @Override
    @Transactional
    public void cancelRentalRequest(Long rentalId) {
        Rental rental = rentalRepo.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Cannot cancel");
        }
        rental.setStatus(RentalStatus.CANCELED_BY_RENTER);
        rentalRepo.save(rental);

        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.AVAILABLE);
        offerRepo.save(offer);
    }

    @Override
    @Transactional
    public void rejectRentalRequest(Long rentalId) {
        Rental rental = rentalRepo.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Cannot reject");
        }
        rental.setStatus(RentalStatus.CANCELED_BY_OWNER);
        rentalRepo.save(rental);

        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.AVAILABLE);
        offerRepo.save(offer);
    }

    @Override
    @Transactional
    public void confirmRentalRequest(Long rentalId) {
        Rental rental = rentalRepo.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Cannot confirm");
        }
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setStartDate(LocalDateTime.now());
        rentalRepo.save(rental);

        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.RENTED);
        offerRepo.save(offer);

        modelMapper.map(rental, RentalViewModel.class);
    }

    @Override
    @Transactional
    public void initiateReturn(Long rentalId) {
        Rental rental = rentalRepo.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new RuntimeException("Cannot return");
        }
        rental.setStatus(RentalStatus.PENDING_FOR_RETURN);
        rentalRepo.save(rental);
    }

    @Override
    @Transactional
    public void confirmReturn(Long rentalId) {
        Rental rental = rentalRepo.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_RETURN) {
            throw new RuntimeException("Cannot confirm return");
        }
        rental.setStatus(RentalStatus.RETURNED);
        rentalRepo.save(rental);
        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.AVAILABLE);
        offerRepo.save(offer);
    }

    @Override
    @Transactional
    public List<RentalViewModel> getOwnerPendingRequests() {
        String owner = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepo.findByOfferOwnerUsernameAndStatus(owner, RentalStatus.PENDING_FOR_CONFIRM)
                .stream()
                .map(rental -> {
                    RentalViewModel viewModel = modelMapper.map(rental, RentalViewModel.class);
                    viewModel.setOwnerContact(rental.getRenter().getEmail());
                    Duration dur = Duration.between(rental.getCreated(), rental.getEndDate());
                    viewModel.setDays((int) dur.toDays());
                    return viewModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RentalViewModel> getMyRentals() {
        String renter = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepo.findByRenterUsername(renter)
                .stream()
                .map(rental -> modelMapper.map(rental, RentalViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RentalViewModel> getOwnerActiveRentals() {
        String owner = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepo.findByOfferOwnerUsernameAndStatus(owner, RentalStatus.ACTIVE)
                .stream()
                .map(rental -> {
                    RentalViewModel viewModel = modelMapper.map(rental, RentalViewModel.class);
                    viewModel.setRenterUsername(rental.getRenter().getUsername());
                    viewModel.setOwnerContact(rental.getRenter().getEmail());
                    return viewModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RentalViewModel> getOwnerPendingReturns() {
        String owner = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepo.findByOfferOwnerUsernameAndStatus(owner, RentalStatus.PENDING_FOR_RETURN)
                .stream()
                .map(rental -> {
                    RentalViewModel viewModel = modelMapper.map(rental, RentalViewModel.class);
                    viewModel.setRenterUsername(rental.getRenter().getUsername());
                    viewModel.setOwnerContact(rental.getRenter().getEmail());
                    return viewModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void autoDecline() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        rentalRepo.findAllByStatusAndCreatedBefore(RentalStatus.PENDING_FOR_CONFIRM, cutoff)
                .forEach(rental -> {
                    rental.setStatus(RentalStatus.CANCELED_BY_SCHEDULER);
                    rentalRepo.save(rental);
                    Offer offer = rental.getOffer();
                    offer.setStatus(OfferStatus.AVAILABLE);
                    offerRepo.save(offer);
                });
    }

}