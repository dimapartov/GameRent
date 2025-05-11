package org.example.gamerent.web.controllers;

import org.example.gamerent.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/brand")
public class BrandController {

    @Value("${brands.page.size}")
    private int pageSize;

    private final BrandService brandService;


    @Autowired
    public BrandController(BrandService brandService){
        this.brandService = brandService;
    }


    @GetMapping("/all")
    public String getBrandsPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                Model model) {
        model.addAttribute("allBrands", brandService.getAllBrandsForView(page, pageSize));
        return "brands-all-page";
    }

}