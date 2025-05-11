package org.example.gamerent.services.impl;

import org.example.gamerent.models.Brand;
import org.example.gamerent.repos.BrandRepository;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.dto.BrandDTO;
import org.example.gamerent.web.viewmodels.BrandViewModel;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository,
                            ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public void createBrand(BrandCreationInputModel newBrandInputModel) {
        Brand brand = modelMapper.map(newBrandInputModel, Brand.class);
        brandRepository.save(brand);
    }

    @Override
    public List<BrandDTO> getAllBrandsDTOs() {
        return brandRepository.findAll()
                .stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<BrandViewModel> getAllBrandsForView(int pageNumber, int pageSize) {
        Pageable pageSettings = PageRequest.of(pageNumber, pageSize);
        Page<Brand> brandsPage = brandRepository.findAll(pageSettings);
        return brandsPage.map(brand -> modelMapper.map(brand, BrandViewModel.class));
    }

}