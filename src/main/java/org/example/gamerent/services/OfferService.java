package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


public interface OfferService {

    //    Для инициализации данных
    void seedOffer(OfferCreationInputModel newOffer, String username);


    OfferCreationInputModel createOffer(OfferCreationInputModel newOffer, MultipartFile photo);

    Page<OfferDemoViewModel> getAllOffersFiltered(
            BigDecimal priceFrom,
            BigDecimal priceTo,
            String brand,
            Boolean myOffers,
            int page,
            int size,
            String sortBy
    );

    OfferViewModel getById(Long id);

}