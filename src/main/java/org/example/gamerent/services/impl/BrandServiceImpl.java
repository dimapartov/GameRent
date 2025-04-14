package org.example.gamerent.services.impl;

import org.example.gamerent.models.Brand;
import org.example.gamerent.repos.BrandRepository;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public BrandCreationInputModel createBrand(BrandCreationInputModel newBrand) {
        Brand brand = modelMapper.map(newBrand, Brand.class);
        brand = brandRepository.save(brand);
        return modelMapper.map(brand, BrandCreationInputModel.class);
    }

}