package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.RentalViewModel;

import java.math.BigDecimal;
import java.util.List;

public interface OfferService {
    OfferViewModel createOffer(OfferViewModel offerVM);
    OfferViewModel getOfferById(Long id);
    List<OfferViewModel> getAllOffers();
    void deleteOffer(Long id);
    List<OfferViewModel> filterOffers(String gameName,
                                      String brandName,
                                      BigDecimal minPrice,
                                      BigDecimal maxPrice,
                                      Integer maxRentalDaysOption);
    OfferViewModel bookOffer(Long offerId, Long renterId);
    OfferViewModel initiateRental(Long offerId, Long renterId);
    OfferViewModel confirmRental(Long offerId);
    RentalViewModel initiateReturn(Long rentalId);
    RentalViewModel confirmReturn(Long rentalId);
}