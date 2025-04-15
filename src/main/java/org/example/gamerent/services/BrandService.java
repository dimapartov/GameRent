package org.example.gamerent.services;

import org.example.gamerent.services.dto.BrandDTO;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;

import java.util.List;


public interface BrandService {

    BrandCreationInputModel createBrand(BrandCreationInputModel newBrand);

    List<BrandDTO> getAllBrandsForOfferCreation();

}