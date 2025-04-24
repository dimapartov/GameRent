package org.example.gamerent.web.controllers;

import org.example.gamerent.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;


    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }


    @GetMapping("/all")
    public String getBrandsPage(Model model) {
        model.addAttribute("allBrands", brandService.getAllBrandsForView());
        return "brand-all-page";
    }

}