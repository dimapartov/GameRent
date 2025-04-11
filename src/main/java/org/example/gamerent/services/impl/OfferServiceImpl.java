package org.example.gamerent.services.impl;

import org.example.gamerent.models.Offer;
import org.example.gamerent.models.Rental;
import org.example.gamerent.models.consts.OfferStatus;
import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.dto.RentalDTO;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,
                            RentalRepository rentalRepository,
                            UserRepository userRepository,
                            ModelMapper modelMapper) {
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OfferViewModel createOffer(OfferViewModel offerVM) {
        Offer offer = modelMapper.map(offerVM, Offer.class);
        offer = offerRepository.save(offer);
        return modelMapper.map(offer, OfferViewModel.class);
    }

    @Override
    public OfferViewModel getOfferById(Long id) {
        Offer offer = offerRepository.findById(id).orElse(null);
        return offer != null ? modelMapper.map(offer, OfferViewModel.class) : null;
    }

    @Override
    public List<OfferViewModel> getAllOffers() {
        List<Offer> offers = offerRepository.findAll();
        Set<Offer> uniqueOffers = new HashSet<>(offers);
        List<OfferViewModel> result = new ArrayList<>();
        for (Offer offer : uniqueOffers) {
            result.add(modelMapper.map(offer, OfferViewModel.class));
        }
        return result;
    }

    @Override
    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    @Override
    public List<OfferViewModel> filterOffers(String gameName, String brandName,
                                             BigDecimal minPrice, BigDecimal maxPrice) {
        List<Offer> offers = new ArrayList<>();
        if (gameName != null && !gameName.isEmpty()) {
            offers.addAll(offerRepository.findByGame_NameContainingIgnoreCase(gameName));
        }
        if (brandName != null && !brandName.isEmpty()) {
            offers.addAll(offerRepository.findByGame_Brand_NameContainingIgnoreCase(brandName));
        }
        if (minPrice != null && maxPrice != null) {
            offers.addAll(offerRepository.findByPriceBetween(minPrice, maxPrice));
        }
        // Removed rental days filtering because Offer doesn't have a 'minRentalDays' property.
        Set<Offer> uniqueOffers = new HashSet<>(offers);
        List<OfferViewModel> result = new ArrayList<>();
        for (Offer offer : uniqueOffers) {
            result.add(modelMapper.map(offer, OfferViewModel.class));
        }
        return result;
    }

    @Override
    public OfferViewModel bookOffer(Long offerId, Long renterId) {
        Offer offer = offerRepository.findById(offerId).orElse(null);
        if (offer != null && offer.getStatus() == OfferStatus.AVAILABLE) {
            offer.setStatus(OfferStatus.BOOKED);
            offerRepository.save(offer);
            return modelMapper.map(offer, OfferViewModel.class);
        }
        return null;
    }

    @Override
    public OfferViewModel initiateRental(Long offerId, Long renterId) {
        Offer offer = offerRepository.findById(offerId).orElse(null);
        if (offer != null && offer.getStatus() == OfferStatus.AVAILABLE) {
            // Создаем DTO для аренды
            RentalDTO rentalDTO = new RentalDTO();
            rentalDTO.setOfferId(offer.getId());
            rentalDTO.setRenterId(renterId);
            rentalDTO.setStartDate(LocalDateTime.now());
            rentalDTO.setStatus(RentalStatus.PENDING);

            // Мапим DTO в объект Rental (используя настроенный ModelMapper)
            Rental rental = modelMapper.map(rentalDTO, Rental.class);
            rentalRepository.save(rental);

            offer.setStatus(OfferStatus.RENTED);
            offerRepository.save(offer);
            return modelMapper.map(offer, OfferViewModel.class);
        }
        return null;
    }

    @Override
    public OfferViewModel confirmRental(Long offerId) {
        Offer offer = offerRepository.findById(offerId).orElse(null);
        if (offer != null && offer.getStatus() == OfferStatus.RENTED) {
            List<Rental> rentals = rentalRepository.findByOffer_Id(offerId);
            for (Rental rental : rentals) {
                if (rental.getStatus() == RentalStatus.PENDING) {
                    rental.setStatus(RentalStatus.ACTIVE);
                    rentalRepository.save(rental);
                }
            }
            return modelMapper.map(offer, OfferViewModel.class);
        }
        return null;
    }

    @Override
    public RentalViewModel initiateReturn(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElse(null);
        if (rental != null && rental.getStatus() == RentalStatus.ACTIVE) {
            rental.setStatus(RentalStatus.PENDING);
            rentalRepository.save(rental);
            return modelMapper.map(rental, RentalViewModel.class);
        }
        return null;
    }

    @Override
    public RentalViewModel confirmReturn(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElse(null);
        if (rental != null && rental.getStatus() == RentalStatus.PENDING) {
            rental.setStatus(RentalStatus.RETURNED);
            rental.setEndDate(LocalDateTime.now());
            rentalRepository.save(rental);

            Offer offer = rental.getOffer();
            offer.setStatus(OfferStatus.AVAILABLE);
            offerRepository.save(offer);
            return modelMapper.map(rental, RentalViewModel.class);
        }
        return null;
    }
}