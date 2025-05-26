package org.example.gamerent.services.impl;

import org.example.gamerent.models.Brand;
import org.example.gamerent.repos.BrandRepository;
import org.example.gamerent.web.viewmodels.BrandViewModel;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private BrandServiceImpl brandService;

    @Test
    void createBrand_shouldSaveMappedEntity() {
        BrandCreationInputModel input = new BrandCreationInputModel();
        Brand brandEntity = new Brand();
        when(modelMapper.map(input, Brand.class)).thenReturn(brandEntity);

        brandService.createBrand(input);

        verify(brandRepository, times(1)).save(brandEntity);
    }

    @Test
    void getAllBrandsDTOs_shouldReturnListOfDTOs() {
        List<Brand> brands = List.of(new Brand(), new Brand(), new Brand());
        when(brandRepository.findAll()).thenReturn(brands);
        when(modelMapper.map(any(Brand.class), eq(org.example.gamerent.services.dto.BrandDTO.class)))
                .thenAnswer(invocation -> new org.example.gamerent.services.dto.BrandDTO());

        var dtos = brandService.getAllBrandsDTOs();

        assertEquals(3, dtos.size());
    }

    @Test
    void getAllBrandsForView_shouldReturnPagedViewModels() {
        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> page = new PageImpl<>(brands, PageRequest.of(1, 2), brands.size());
        when(brandRepository.findAll(PageRequest.of(1, 2))).thenReturn(page);
        when(modelMapper.map(any(Brand.class), eq(BrandViewModel.class))).thenAnswer(invocation -> new BrandViewModel());

        var result = brandService.getAllBrandsForView(1, 2);

        assertEquals(2, result.getContent().size());
    }

}