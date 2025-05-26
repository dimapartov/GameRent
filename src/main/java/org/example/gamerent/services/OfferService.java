package org.example.gamerent.services;

import org.example.gamerent.models.consts.OfferDifficulty;
import org.example.gamerent.models.consts.OfferGenre;
import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.OfferUpdateInputModel;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;


public interface OfferService {

    //    Для инициализации данных
    void seedOffer(OfferCreationInputModel newOfferInputModel, String offerOwnerUsername);


    Long createOffer(OfferCreationInputModel newOfferInputModel);

    Page<OfferDemoViewModel> getAllOffersFiltered(
            BigDecimal priceFrom,
            BigDecimal priceTo,
            String brandName,
            Boolean myOffers,
            int pageNumber,
            int pageSize,
            String sortBy,
            String searchTerm,
            List<OfferGenre> genres,
            List<OfferDifficulty> difficulties
    );

    OfferViewModel getOfferById(Long offerId);

    void updateOffer(Long offerId, OfferUpdateInputModel offerUpdateInputModel);

    void deleteOfferById(Long offerId);

    OfferUpdateInputModel getOfferUpdateInputModel(Long offerId);

}