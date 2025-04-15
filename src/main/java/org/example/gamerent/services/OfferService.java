package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface OfferService {

    OfferCreationInputModel createOffer(OfferCreationInputModel newOffer, MultipartFile image);

    List<OfferDemoViewModel> getAllOffersDemoViewModels();

}