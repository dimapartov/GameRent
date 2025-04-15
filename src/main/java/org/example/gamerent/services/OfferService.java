package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;


public interface OfferService {

    OfferCreationInputModel createOffer(OfferCreationInputModel newOffer, MultipartFile photo);

    List<OfferDemoViewModel> getAllOffersDemoViewModels();

    // Новый метод для универсальной фильтрации офферов
    List<OfferDemoViewModel> getOffersFiltered(BigDecimal priceFrom, BigDecimal priceTo, String brand, Boolean myOffers);

}