package org.example.gamerent.web.controllers;

import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.dto.OfferFiltersDTO;
import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/offer")
public class OfferController {

    @Value("${offer.page.size}")
    private int pageSize;

    private final OfferService offerService;
    private final BrandService brandService;


    @Autowired
    public OfferController(OfferService offerService, BrandService brandService) {
        this.offerService = offerService;
        this.brandService = brandService;
    }


    @GetMapping("/create")
    public String getOfferCreationPage(Model model) {
        model.addAttribute("allBrands", brandService.getAllBrands());
        return "offer-creation-page";
    }

    @ModelAttribute("newOfferInputModel")
    public OfferCreationInputModel initOffer() {
        return new OfferCreationInputModel();
    }

    @PostMapping("/create")
    public String createOffer(@ModelAttribute("newOfferInputModel") OfferCreationInputModel newOfferInputModel,
                              @RequestParam("file") MultipartFile file) {
        offerService.createOffer(newOfferInputModel, file);
        return "redirect:/offer/all";
    }

    @GetMapping("/all")
    public String getAllOffersFilteredPage(@ModelAttribute("filters") OfferFiltersDTO filters, @RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<OfferDemoViewModel> offersPage = offerService.getAllOffersFiltered(
                filters.getPriceFrom(),
                filters.getPriceTo(),
                filters.getBrand(),
                filters.getMyOffers(),
                page,
                pageSize
        );

        model.addAttribute("offersPage", offersPage);
        model.addAttribute("allBrands", brandService.getAllBrands());
        return "offer-all-filtered-page";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          Model model) {
        OfferViewModel offer = offerService.getById(id);
        model.addAttribute("offer", offer);
        if (!model.containsAttribute("rentalInput")) {
            RentalRequestInputModel input = new RentalRequestInputModel();
            input.setOfferId(id);
            model.addAttribute("rentalInput", input);
        }

        return "offer-details-page";
    }

}