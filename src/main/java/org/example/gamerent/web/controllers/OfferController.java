package org.example.gamerent.web.controllers;

import jakarta.validation.Valid;
import org.example.gamerent.models.consts.OfferDifficulty;
import org.example.gamerent.models.consts.OfferGenre;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.dto.OfferFiltersDTO;
import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.OfferUpdateInputModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/offer")
public class OfferController {

    @Value("${offers.page.size}")
    private int pageSize;

    private final OfferService offerService;
    private final BrandService brandService;


    @Autowired
    public OfferController(OfferService offerService, BrandService brandService) {
        this.offerService = offerService;
        this.brandService = brandService;
    }


    @ModelAttribute("filters")
    public OfferFiltersDTO initFiltersDTO() {
        return new OfferFiltersDTO();
    }

    @ModelAttribute("newOfferInputModel")
    public OfferCreationInputModel initOfferCreationInputModel() {
        return new OfferCreationInputModel();
    }


    @GetMapping("/create")
    public String getOfferCreationPage(Model model) {
        model.addAttribute("allBrands", brandService.getAllBrandsDTOs());
        model.addAttribute("allGenres", OfferGenre.values());
        model.addAttribute("allDifficulties", OfferDifficulty.values());
        return "offer-creation-page";
    }

    @PostMapping("/create")
    public String createOffer(@Valid @ModelAttribute("newOfferInputModel") OfferCreationInputModel newOfferInputModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("newOfferInputModel", newOfferInputModel);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "newOfferInputModel", bindingResult);
            return "redirect:/offer/create";
        }
        Long newId = offerService.createOffer(newOfferInputModel);
        return "redirect:/offer/" + newId;
    }

    @GetMapping("/all")
    public String getAllOffersFilteredPage(@ModelAttribute("filters") OfferFiltersDTO filters,
                                           @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                           @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                           Model model) {
        Page<OfferDemoViewModel> offersPage = offerService.getAllOffersFiltered(
                filters.getPriceFrom(),
                filters.getPriceTo(),
                filters.getBrand(),
                filters.getMyOffers(),
                pageNumber,
                pageSize,
                filters.getSortBy(),
                filters.getSearchTerm(),
                filters.getGenres(),
                filters.getDifficulties()
        );
        model.addAttribute("allGenres", OfferGenre.values());
        model.addAttribute("allDifficulties", OfferDifficulty.values());
        model.addAttribute("offersPage", offersPage);
        model.addAttribute("allBrands", brandService.getAllBrandsDTOs());
        return "offers-all-filtered-page";
    }

    @GetMapping("/{id}")
    public String getOfferDetailsPage(@PathVariable Long id,
                                      @ModelAttribute("rentalInput") RentalRequestInputModel rentalInput,
                                      Model model) {
        OfferViewModel offer = offerService.getOfferById(id);
        model.addAttribute("offer", offer);
        rentalInput.setOfferId(id);

        if (!model.containsAttribute("offerUpdateInputModel")) {
            try {
                OfferUpdateInputModel offerUpdateInputModel = offerService.getOfferUpdateInputModel(id);
                model.addAttribute("offerUpdateInputModel", offerUpdateInputModel);
            } catch (RuntimeException ignored) {
                model.addAttribute("offerUpdateInputModel", new OfferUpdateInputModel());
            }
        }
        return "offer-details-page";
    }

    @PostMapping("/{id}/edit")
    public String editOffer(@PathVariable Long id,
                            @Valid @ModelAttribute("offerUpdateInputModel") OfferUpdateInputModel offerUpdateInputModel,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("offerUpdateInputModel", offerUpdateInputModel);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "offerUpdateInputModel", bindingResult);
            return "redirect:/offer/" + id;
        }
        offerService.updateOffer(id, offerUpdateInputModel);
        redirectAttributes.addFlashAttribute("success", "Оффер успешно обновлён");
        return "redirect:/offer/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteOffer(@PathVariable Long id) {
        offerService.deleteOfferById(id);
        return "redirect:/offer/all";
    }

}