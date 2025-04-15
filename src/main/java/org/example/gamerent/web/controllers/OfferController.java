package org.example.gamerent.web.controllers;


import org.example.gamerent.models.Offer;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
        model.addAttribute("brandList", brandService.getAllBrandsForOfferCreation());
        return "offer-creation-page";
    }

    @ModelAttribute("newOfferInputModel")
    public OfferCreationInputModel initOffer() {
        return new OfferCreationInputModel();
    }

    @PostMapping("/create")
    public String createOffer(OfferCreationInputModel newOfferInputModel, @RequestParam("file") MultipartFile file) {
        offerService.createOffer(newOfferInputModel, file);
        return "redirect:/offer/success";
    }

    @GetMapping("/success")
    public String getOfferCreatedPage() {
        return "offer-created-page";
    }

    @GetMapping("/{id}")
    public String viewOffer(@PathVariable Long id, Model model) {
        Offer offer = offerService.getOfferViewModelByOfferId(id);
        model.addAttribute("offer", offer);
        return "offer-details-page";
    }

}