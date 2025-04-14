package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.BrandViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;

import java.util.List;


public interface BrandService {

    BrandCreationInputModel createBrand(BrandCreationInputModel newBrand);

    BrandViewModel getBrandById(Long id);

    List<BrandViewModel> getAllBrands();

    void deleteBrand(Long id);

    List<OfferViewModel> getOffersByBrand(Long brandId);

}