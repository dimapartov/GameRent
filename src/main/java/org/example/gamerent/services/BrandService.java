package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;


public interface BrandService {

    BrandCreationInputModel createBrand(BrandCreationInputModel newBrand);

}