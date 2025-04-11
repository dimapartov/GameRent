package org.example.gamerent.services.impl;

import org.example.gamerent.models.Brand;
import org.example.gamerent.models.Offer;
import org.example.gamerent.repos.BrandRepository;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.web.viewmodels.BrandViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository,
                            OfferRepository offerRepository,
                            ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandViewModel createBrand(BrandViewModel brandVM) {
        Brand brand = modelMapper.map(brandVM, Brand.class);
        brand = brandRepository.save(brand);
        return modelMapper.map(brand, BrandViewModel.class);
    }

    @Override
    public BrandViewModel getBrandById(Long id) {
        Brand brand = brandRepository.findById(id).orElse(null);
        return brand != null ? modelMapper.map(brand, BrandViewModel.class) : null;
    }

    @Override
    public List<BrandViewModel> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        List<BrandViewModel> result = new ArrayList<>();
        for (Brand brand : brands) {
            result.add(modelMapper.map(brand, BrandViewModel.class));
        }
        return result;
    }

    @Override
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public List<OfferViewModel> getOffersByBrand(Long brandId) {
        List<Offer> offers = offerRepository.findAll();
        List<OfferViewModel> result = new ArrayList<>();
        for (Offer offer : offers) {
            if (offer.getGame() != null && offer.getGame().getBrand() != null &&
                    offer.getGame().getBrand().getId().equals(brandId)) {
                result.add(modelMapper.map(offer, OfferViewModel.class));
            }
        }
        return result;
    }
}