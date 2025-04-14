package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.springframework.web.multipart.MultipartFile;


public interface OfferService {

    OfferCreationInputModel createOffer(OfferCreationInputModel newOffer, MultipartFile image);

}