package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.OfferUpdateInputModel;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


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
            String searchTerm
    );

    OfferViewModel getOfferById(Long offerId);

    void updateOffer(Long offerId, OfferUpdateInputModel offerUpdateInputModel);

    void deleteOfferById(Long offerId);

    OfferUpdateInputModel getOfferUpdateInputModel(Long offerId);

}