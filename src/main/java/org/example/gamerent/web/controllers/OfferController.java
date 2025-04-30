package org.example.gamerent.web.controllers;

import jakarta.validation.Valid;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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


    @ModelAttribute("filters")
    public OfferFiltersDTO initFiltersDTO() {
        return new OfferFiltersDTO();
    }

    @ModelAttribute("rentalInput")
    public RentalRequestInputModel initRentalRequestInputModel() {
        return new RentalRequestInputModel();
    }

    @ModelAttribute("newOfferInputModel")
    public OfferCreationInputModel initOfferCreationInputModel() {
        return new OfferCreationInputModel();
    }


    @GetMapping("/create")
    public String getOfferCreationPage(Model model) {
        model.addAttribute("allBrands", brandService.getAllBrandsDTOs());
        return "offer-creation-page";
    }

    @PostMapping("/create")
    public String createOffer(@ModelAttribute("newOfferInputModel") OfferCreationInputModel newOfferInputModel,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        Long newId = offerService.createOffer(newOfferInputModel, file);
        return "redirect:/offer/" + newId;
    }

    @GetMapping("/all")
    public String getAllOffersFilteredPage(
            @ModelAttribute("filters") OfferFiltersDTO filters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
            Model model) {
        System.out.println("Вызван метод контроллера /all");
        Page<OfferDemoViewModel> offersPage = offerService.getAllOffersFiltered(
                filters.getPriceFrom(),
                filters.getPriceTo(),
                filters.getBrand(),
                filters.getMyOffers(),
                page,
                pageSize,
                filters.getSortBy(),
                filters.getSearchTerm()
        );
        model.addAttribute("offersPage", offersPage);
        model.addAttribute("allBrands", brandService.getAllBrandsDTOs());
        System.out.println("Контроллер отработал, возврат страницы");
        return "offer-all-filtered-page";
    }

    @GetMapping("/{id}")
    public String getOfferDetailsPage(@PathVariable Long id,
                                      @ModelAttribute("rentalInput") RentalRequestInputModel rentalInput,
                                      Model model) {
        OfferViewModel offer = offerService.getById(id);
        model.addAttribute("offer", offer);
        rentalInput.setOfferId(id);

        try {
            OfferUpdateInputModel updateModel = offerService.getOfferUpdateModel(id);
            model.addAttribute("offerUpdateInputModel", updateModel);
        } catch (RuntimeException ignored) {
        }

        return "offer-details-page";
    }

    @PostMapping("/{id}/edit")
    public String editOffer(@PathVariable Long id,
                            @Valid @ModelAttribute("offerUpdateInputModel") OfferUpdateInputModel input,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Некорректные данные");
            return "redirect:/offer/" + id;
        }
        try {
            offerService.updateOffer(id, input);
            redirectAttributes.addFlashAttribute("success", "Оффер успешно обновлён");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/offer/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteOffer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            offerService.deleteOffer(id);
            redirectAttributes.addFlashAttribute("success", "Оффер удалён");
            return "redirect:/offer/all";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/offer/" + id;
        }
    }


}