package org.example.gamerent.services.impl;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MailSenderService mailSenderService;


    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository,
                             OfferRepository offerRepository,
                             UserRepository userRepository,
                             ModelMapper modelMapper,
                             MailSenderService mailSenderService) {
        this.rentalRepository = rentalRepository;
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.mailSenderService = mailSenderService;
    }


    @Override
    public void createRentalRequest(RentalRequestInputModel rentalRequestInputModel) {
        Offer offerModel = offerRepository.findById(rentalRequestInputModel.getOfferId()).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        if (offerModel.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен");
        }
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User offerRenter = userRepository.findUserByUsername(currentUserUsername).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (offerModel.getOwner().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Нельзя арендовать собственный оффер");
        }
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setOffer(offerModel);
        rentalDTO.setRenter(offerRenter);
        rentalDTO.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        rentalDTO.setStartDate(null);
        rentalDTO.setEndDate(null);
        rentalDTO.setDays(rentalRequestInputModel.getDays());
        Rental rentalModel = modelMapper.map(rentalDTO, Rental.class);
        rentalRepository.save(rentalModel);
        offerModel.setStatus(OfferStatus.BOOKED);
        offerRepository.save(offerModel);

        String ownerEmail = offerModel.getOwner().getEmail();
        String subject = "Новый запрос на аренду вашей игры";
        String body = String.format(
                "Здравствуйте, %s!\n\nПользователь %s отправил запрос на аренду вашей игры \"%s\" " +
                        "на %d дней.\n\nПерейдите в личный кабинет, чтобы подтвердить или отклонить запрос.",
                offerModel.getOwner().getUsername(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                offerModel.getGameName(),
                rentalRequestInputModel.getDays()
        );
        mailSenderService.sendSimpleMail(ownerEmail, subject, body);
    }

    @Override
    public void cancelRentalRequest(Long rentalId) {
        Rental rentalModel = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rentalModel.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Невозможно отменить запрос");
        }
        rentalModel.setStatus(RentalStatus.CANCELED_BY_RENTER);
        rentalRepository.save(rentalModel);
        Offer offerModel = rentalModel.getOffer();
        offerModel.setStatus(OfferStatus.AVAILABLE);
        offerRepository.save(offerModel);
    }

    @Override
    public void rejectRentalRequest(Long rentalId) {
        Rental rentalModel = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rentalModel.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Невозможно отклонить запрос");
        }
        rentalModel.setStatus(RentalStatus.CANCELED_BY_OWNER);
        rentalRepository.save(rentalModel);
        Offer offerModel = rentalModel.getOffer();
        offerModel.setStatus(OfferStatus.AVAILABLE);
        offerRepository.save(offerModel);

        String subject = "Ваш запрос на аренду игры отклонен";
        String body = String.format(
                "Здравствуйте, %s!\n\nПользователь %s отклонил ваш запрос на аренду игры \"%s\" на %d дней.",
                rentalModel.getRenter().getUsername(), offerModel.getOwner().getUsername(), offerModel.getGameName(), rentalModel.getDays()
        );
        mailSenderService.sendSimpleMail(rentalModel.getRenter().getEmail(), subject, body);
    }

    @Override
    public void confirmRentalRequest(Long rentalId) {
        Rental rentalModel = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rentalModel.getStatus() != RentalStatus.PENDING_FOR_CONFIRM) {
            throw new RuntimeException("Невозможно подтвердить запрос");
        }
        rentalModel.setStatus(RentalStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();
        rentalModel.setStartDate(now);
        rentalModel.setEndDate(now.plusDays(rentalModel.getDays()));
        rentalRepository.save(rentalModel);
        Offer offerModel = rentalModel.getOffer();
        offerModel.setStatus(OfferStatus.RENTED);
        offerRepository.save(offerModel);
        String subject = "Ваш запрос на аренду игры подтвержден";
        String body = String.format(
                "Здравствуйте, %s!\n\nПользователь %s подтвердил ваш запрос на аренду игры \"%s\" на %d дней.",
                rentalModel.getRenter().getUsername(), offerModel.getOwner().getUsername(), offerModel.getGameName(), rentalModel.getDays()
        );
        mailSenderService.sendSimpleMail(rentalModel.getRenter().getEmail(), subject, body);
    }

    @Override
    public void initiateRentalReturn(Long rentalId) {
        Rental rentalModel = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rentalModel.getStatus() != RentalStatus.ACTIVE) {
            throw new RuntimeException("Невозможно вернуть");
        }
        rentalModel.setStatus(RentalStatus.PENDING_FOR_RETURN);
        rentalRepository.save(rentalModel);
    }

    @Override
    public void confirmRentalReturn(Long rentalId) {
        Rental rentalModel = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Аренда не найдена"));
        if (rentalModel.getStatus() != RentalStatus.PENDING_FOR_RETURN) {
            throw new RuntimeException("Невозможно подтвердить возврат");
        }
        rentalModel.setStatus(RentalStatus.RETURNED);
        rentalRepository.save(rentalModel);
        Offer offerModel = rentalModel.getOffer();
        offerModel.setStatus(OfferStatus.AVAILABLE);
        offerRepository.save(offerModel);
    }

    @Override
    public Page<RentalViewModel> getPendingRequestsForOwner(int pageNumber, int pageSize) {
        String rentalOwner = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageSettings = PageRequest.of(pageNumber, pageSize);
        Page<Rental> rentals = rentalRepository.findByOfferOwnerUsernameAndStatus(rentalOwner, RentalStatus.PENDING_FOR_CONFIRM, pageSettings);

        return rentals.map(rental -> {
            RentalViewModel rentalViewModel = modelMapper.map(rental, RentalViewModel.class);
            rentalViewModel.setOwnerContact(rental.getRenter().getEmail());
            rentalViewModel.setDays(rental.getDays());
            return rentalViewModel;
        });
    }

    @Override
    public Page<RentalViewModel> getMyRentalsByStatus(RentalStatus rentalStatus, int pageNumber, int pageSize) {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageSettings = PageRequest.of(pageNumber, pageSize);
        Page<Rental> rentals = rentalRepository.findByRenterUsernameAndStatus(currentUserUsername, rentalStatus, pageSettings);

        return rentals.map(rental -> {
            RentalViewModel rentalViewModel = modelMapper.map(rental, RentalViewModel.class);
            rentalViewModel.setOwnerContact(rental.getOffer().getOwner().getEmail());
            if (rental.getStatus() == RentalStatus.PENDING_FOR_CONFIRM) {
                rentalViewModel.setDays(rental.getDays());
            }
            return rentalViewModel;
        });
    }

    @Override
    public Page<RentalViewModel> getActiveRentalsForOwner(int pageNumber, int pageSize) {
        String rentalOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageSettings = PageRequest.of(pageNumber, pageSize);
        Page<Rental> rentals = rentalRepository.findByOfferOwnerUsernameAndStatus(rentalOwnerUsername, RentalStatus.ACTIVE, pageSettings);

        return rentals.map(rental -> {
            RentalViewModel rentalViewModel = modelMapper.map(rental, RentalViewModel.class);
            rentalViewModel.setRenterUsername(rental.getRenter().getUsername());
            rentalViewModel.setOwnerContact(rental.getRenter().getEmail());
            return rentalViewModel;
        });
    }

    @Override
    public Page<RentalViewModel> getPendingReturnsForOwner(int pageNumber, int pageSize) {
        String rentalOwnerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageSettings = PageRequest.of(pageNumber, pageSize);
        Page<Rental> rentals = rentalRepository.findByOfferOwnerUsernameAndStatus(rentalOwnerUsername, RentalStatus.PENDING_FOR_RETURN, pageSettings);

        return rentals.map(rental -> {
            RentalViewModel rentalViewModel = modelMapper.map(rental, RentalViewModel.class);
            rentalViewModel.setRenterUsername(rental.getRenter().getUsername());
            rentalViewModel.setOwnerContact(rental.getRenter().getEmail());
            return rentalViewModel;
        });
    }

    @Override
    public Page<RentalViewModel> getCompletedRentalsForOwner(int pageNumber, int pageSize) {
        String rentalOwnerUsername = SecurityContextHolder
                .getContext().getAuthentication().getName();
        Pageable pageSettings = PageRequest.of(pageNumber, pageSize);
        Page<Rental> rentals = rentalRepository
                .findByOfferOwnerUsernameAndStatus(
                        rentalOwnerUsername,
                        RentalStatus.RETURNED,
                        pageSettings
                );
        return rentals.map(rental -> {
            RentalViewModel vm = modelMapper.map(rental, RentalViewModel.class);
            vm.setRenterUsername(rental.getRenter().getUsername());
            vm.setOwnerContact(rental.getRenter().getEmail());
            return vm;
        });
    }

    @Override
    public void autoDeclineRentalRequest() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        rentalRepository.findAllByStatusAndCreatedBefore(RentalStatus.PENDING_FOR_CONFIRM, cutoff)
                .forEach(rental -> {
                    rental.setStatus(RentalStatus.CANCELED_BY_SCHEDULER);
                    rentalRepository.save(rental);
                    Offer offerModel = rental.getOffer();
                    offerModel.setStatus(OfferStatus.AVAILABLE);
                    offerRepository.save(offerModel);
                });
    }

}