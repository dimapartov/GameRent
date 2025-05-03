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

    private final RentalRepository rentalRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository,
                             OfferRepository offerRepository,
                             UserRepository userRepository,
                             ModelMapper modelMapper) {
        this.rentalRepository = rentalRepository;
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional
    public void createRentalRequest(RentalRequestInputModel rentalRequestInputModel) {
        Offer offer = offerRepository.findById(rentalRequestInputModel.getOfferId()).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        if (offer.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен");
        }
        if (rentalRequestInputModel.getDays() < offer.getMinRentalDays() || rentalRequestInputModel.getDays() > offer.getMaxRentalDays()) {
            throw new RuntimeException("Некорректные сроки аренды");
        }
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User renter = userRepository.findUserByUsername(currentUserUsername).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setOffer(offer);
        rentalDTO.setRenter(renter);
        rentalDTO.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        rentalDTO.setStartDate(null);
        rentalDTO.setEndDate(LocalDateTime.now().plusDays(rentalRequestInputModel.getDays()));
        Rental rental = modelMapper.map(rentalDTO, Rental.class);
        rentalRepository.save(rental);
        offer.setStatus(OfferStatus.BOOKED);
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void cancelRentalRequest(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Невозможно отменить запрос");
        }
        rental.setStatus(RentalStatus.CANCELED_BY_RENTER);
        rentalRepository.save(rental);
        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.AVAILABLE);
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void rejectRentalRequest(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Невозможно отклонить запрос");
        }
        rental.setStatus(RentalStatus.CANCELED_BY_OWNER);
        rentalRepository.save(rental);
        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.AVAILABLE);
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void confirmRentalRequest(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Невозможно подтвердить запрос");
        }
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setStartDate(LocalDateTime.now());
        rentalRepository.save(rental);
        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.RENTED);
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void initiateRentalReturn(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new RuntimeException("Невозможно вернуть");
        }
        rental.setStatus(RentalStatus.PENDING_FOR_RETURN);
        rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public void confirmRentalReturn(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rental.getStatus() != RentalStatus.PENDING_FOR_RETURN) {
            throw new RuntimeException("Невозможно подтвердить возврат");
        }
        rental.setStatus(RentalStatus.RETURNED);
        rentalRepository.save(rental);
        Offer offer = rental.getOffer();
        offer.setStatus(OfferStatus.AVAILABLE);
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public List<RentalViewModel> getPendingRequestsForOwner() {
        String owner = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepository.findByOfferOwnerUsernameAndStatus(owner, RentalStatus.PENDING_FOR_CONFIRM)
                .stream()
                .map(rental -> {
                    RentalViewModel rentalViewModel = modelMapper.map(rental, RentalViewModel.class);
                    rentalViewModel.setOwnerContact(rental.getRenter().getEmail());
                    Duration duration = Duration.between(rental.getCreated(), rental.getEndDate());
                    rentalViewModel.setDays((int) duration.toDays());
                    return rentalViewModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RentalViewModel> getMyRentals() {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepository.findByRenterUsername(currentUserUsername)
                .stream()
                .map(rental -> modelMapper.map(rental, RentalViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RentalViewModel> getActiveRentalsForOwner() {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepository.findByOfferOwnerUsernameAndStatus(currentUserUsername, RentalStatus.ACTIVE)
                .stream()
                .map(rental -> {
                    RentalViewModel rentalViewModel = modelMapper.map(rental, RentalViewModel.class);
                    rentalViewModel.setRenterUsername(rental.getRenter().getUsername());
                    rentalViewModel.setOwnerContact(rental.getRenter().getEmail());
                    return rentalViewModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RentalViewModel> getPendingReturnsForOwner() {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return rentalRepository.findByOfferOwnerUsernameAndStatus(currentUserUsername, RentalStatus.PENDING_FOR_RETURN)
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
    public void autoDeclineRentalRequest() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        rentalRepository.findAllByStatusAndCreatedBefore(RentalStatus.PENDING_FOR_CONFIRM, cutoff)
                .forEach(rental -> {
                    rental.setStatus(RentalStatus.CANCELED_BY_SCHEDULER);
                    rentalRepository.save(rental);
                    Offer offer = rental.getOffer();
                    offer.setStatus(OfferStatus.AVAILABLE);
                    offerRepository.save(offer);
                });
    }

}