package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.BrandViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;

import java.util.List;

public interface BrandService {
    BrandViewModel createBrand(BrandViewModel brandVM);
    BrandViewModel getBrandById(Long id);
    List<BrandViewModel> getAllBrands();
    void deleteBrand(Long id);

    List<OfferViewModel> getOffersByBrand(Long brandId);
}