package org.example.gamerent.web.controllers;


import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping("/offer")
public class OfferController {

    private OfferService offerService;
    private BrandService brandService;


    @Autowired
    public OfferController(OfferService offerService, BrandService brandService) {
        this.offerService = offerService;
        this.brandService = brandService;
    }


    @GetMapping("/create")
    public String getOfferCreationPage(Model model) {
        model.addAttribute("allBrands", brandService.getAllBrandsForOfferCreation());
        return "offer-creation-page";
    }

    @ModelAttribute("newOfferInputModel")
    public OfferCreationInputModel initOffer() {
        return new OfferCreationInputModel();
    }

    @PostMapping("/create")
    public String createOffer(OfferCreationInputModel newOfferInputModel, @RequestParam("file") MultipartFile file) {
        offerService.createOffer(newOfferInputModel, file);
        return "redirect:/offer/";
    }

    @GetMapping("/all")
    public String getAllOffersPage(Model model) {
        model.addAttribute("allOffers", offerService.getAllOffersDemoViewModels());
        return "offer-all-page";
    }

    // Универсальный endpoint для отображения офферов с фильтрацией и режимом "Мои объявления"
    @GetMapping("/universal")
    public String getOffersUniversal(
            @RequestParam(value = "priceFrom", required = false) BigDecimal priceFrom,
            @RequestParam(value = "priceTo", required = false) BigDecimal priceTo,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "myOffers", required = false) Boolean myOffers,
            Model model) {

        List<OfferDemoViewModel> offers = offerService.getOffersFiltered(priceFrom, priceTo, brand, myOffers);
        model.addAttribute("offers", offers);

        // Для заполнения выпадающего списка брендов в форме фильтрации
        model.addAttribute("allBrands", brandService.getAllBrandsForOfferCreation());
        return "offer-universal-page";
    }
}