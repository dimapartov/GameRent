package org.example.gamerent.web.controllers;

import jakarta.validation.Valid;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.RentalService;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferUpdateInputModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/rental")
public class RentalController {

    private RentalService rentalService;
    private OfferService offerService;


    @Autowired
    public RentalController(RentalService rentalService, OfferService offerService) {
        this.rentalService = rentalService;
        this.offerService = offerService;
    }


    @ModelAttribute("rentalInput")
    public RentalRequestInputModel initRentalRequestInputModel() {
        return new RentalRequestInputModel();
    }


    @GetMapping("/owner/dashboard")
    public String getOwnerDashboardPage(Model model) {
        model.addAttribute("pendingRequests", rentalService.getPendingRequestsForOwner());
        model.addAttribute("activeRentals", rentalService.getActiveRentalsForOwner());
        model.addAttribute("pendingReturns", rentalService.getPendingReturnsForOwner());
        return "rentals-owner-dashboard-page";
    }

    @PostMapping("/owner/{id}/confirm")
    public String confirmRentalRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rentalService.confirmRentalRequest(id);
        redirectAttributes.addFlashAttribute("success", "Аренда подтверждена");
        return "redirect:/rental/owner/dashboard";
    }

    @PostMapping("/owner/{id}/reject")
    public String rejectRentalRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rentalService.rejectRentalRequest(id);
        redirectAttributes.addFlashAttribute("success", "Заявка отклонена владельцем");
        return "redirect:/rental/owner/dashboard";
    }

    @PostMapping("/owner/{id}/confirm-return")
    public String confirmRentalReturn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rentalService.confirmRentalReturn(id);
        redirectAttributes.addFlashAttribute("success", "Возврат подтверждён");
        return "redirect:/rental/owner/dashboard";
    }

    @GetMapping("/my")
    public String getMyRentalsPage(Model model) {
        model.addAttribute("myRentals", rentalService.getMyRentals());
        return "rentals-my-page";
    }

    @PostMapping("/create")
    public String createRentalRequest(
            @Valid @ModelAttribute("rentalInput") RentalRequestInputModel rentalInput,
            BindingResult bindingResult,
            Model model,                               // добавили Model
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Получаем оффер для отображения на странице
            OfferViewModel offer = offerService.getOfferById(rentalInput.getOfferId());
            model.addAttribute("offer", offer);

            // Получаем модель данных для редактирования оффера (чтобы не было NPE в шаблоне)
            try {
                model.addAttribute("offerUpdateInputModel",
                        offerService.getOfferUpdateInputModel(offer.getId()));
            } catch (RuntimeException ignored) {
                model.addAttribute("offerUpdateInputModel", new OfferUpdateInputModel());
            }

            // Здесь возвращаем тот же view, без redirect
            return "offer-details-page";
        }

        try {
            rentalService.createRentalRequest(rentalInput);
            redirectAttributes.addFlashAttribute("success", "Заявка отправлена");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/offer/" + rentalInput.getOfferId();
    }

    @PostMapping("/{id}/cancel")
    public String cancelRentalRequest(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        rentalService.cancelRentalRequest(id);
        redirectAttributes.addFlashAttribute("success", "Заявка отменена");
        return "redirect:/rental/my";
    }

    @PostMapping("/{id}/return")
    public String initiateRentalReturn(@PathVariable Long id,
                                       RedirectAttributes redirectAttributes) {
        rentalService.initiateRentalReturn(id);
        redirectAttributes.addFlashAttribute("success", "Запрос на возврат отправлен");
        return "redirect:/rental/my";
    }

}