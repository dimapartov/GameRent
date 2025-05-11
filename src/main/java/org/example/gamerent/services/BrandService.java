package org.example.gamerent.services;

import org.example.gamerent.services.dto.BrandDTO;
import org.example.gamerent.web.viewmodels.BrandViewModel;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.springframework.data.domain.Page;

import java.util.List;


public interface BrandService {

    void createBrand(BrandCreationInputModel newBrandInputModel);

    List<BrandDTO> getAllBrandsDTOs();

    Page<BrandViewModel> getAllBrandsForView(int pageNumber, int pageSize);

}