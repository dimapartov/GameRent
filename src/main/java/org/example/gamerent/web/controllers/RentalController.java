package org.example.gamerent.web.controllers;

import jakarta.validation.Valid;
import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.RentalService;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferUpdateInputModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/rental")
public class RentalController {

    private static final Logger log = LoggerFactory.getLogger(RentalController.class);

    @Value("${rentals.page.size}")
    private int pageSize;
    private final RentalService rentalService;
    private final OfferService offerService;


    @Autowired
    public RentalController(RentalService rentalService, OfferService offerService) {
        this.rentalService = rentalService;
        this.offerService = offerService;
    }


    @ModelAttribute("rentalInput")
    public RentalRequestInputModel initRentalRequestInputModel() {
        return new RentalRequestInputModel();
    }


    @GetMapping("/my")
    public String getMyRentalsPage(@RequestParam(value = "activePage", defaultValue = "0") int activePage,
                                   @RequestParam(value = "pendingPage", defaultValue = "0") int pendingPage,
                                   @RequestParam(value = "returnedPage", defaultValue = "0") int returnedPage,
                                   @RequestParam(value = "pendingReturnPage", defaultValue = "0") int pendingReturnPage,
                                   @RequestParam(value = "canceledByRenterPage", defaultValue = "0") int canceledByRenterPage,
                                   @RequestParam(value = "canceledByOwnerPage", defaultValue = "0") int canceledByOwnerPage,
                                   @RequestParam(value = "tab", defaultValue = "pendingForConfirm") String tab,
                                   Model model) {
        model.addAttribute("pendingRentals", rentalService.getMyRentalsByStatus(RentalStatus.PENDING_FOR_CONFIRM, pendingPage, pageSize));
        model.addAttribute("activeRentals", rentalService.getMyRentalsByStatus(RentalStatus.ACTIVE, activePage, pageSize));
        model.addAttribute("returnedRentals", rentalService.getMyRentalsByStatus(RentalStatus.RETURNED, returnedPage, pageSize));
        model.addAttribute("pendingReturnRentals", rentalService.getMyRentalsByStatus(RentalStatus.PENDING_FOR_RETURN, pendingReturnPage, pageSize));
        model.addAttribute("canceledByRenter", rentalService.getMyRentalsByStatus(RentalStatus.CANCELED_BY_RENTER, canceledByRenterPage, pageSize));
        model.addAttribute("canceledByOwner", rentalService.getMyRentalsByStatus(RentalStatus.CANCELED_BY_OWNER, canceledByOwnerPage, pageSize));
        model.addAttribute("currentTab", tab);
        log.info("Request to GET rental/my page");
        return "rentals-my-page";
    }

    @PostMapping("/create")
    public String createRentalRequest(@Valid @ModelAttribute("rentalInput") RentalRequestInputModel rentalInput,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            OfferViewModel offer = offerService.getOfferById(rentalInput.getOfferId());
            model.addAttribute("offer", offer);
            try {
                model.addAttribute("offerUpdateInputModel", offerService.getOfferUpdateInputModel(offer.getId()));
            } catch (RuntimeException ignored) {
                model.addAttribute("offerUpdateInputModel", new OfferUpdateInputModel());
            }
            return "offer-details-page";
        }
        rentalService.createRentalRequest(rentalInput);
        redirectAttributes.addFlashAttribute("success", "Заявка отправлена");
        log.info("Request to POST /rental/create");
        return "redirect:/offer/" + rentalInput.getOfferId();
    }

    @PostMapping("/{id}/cancel")
    public String cancelRentalRequest(@PathVariable Long id,
                                      @RequestParam(value = "tab", defaultValue = "pendingForConfirm") String tab,
                                      RedirectAttributes redirectAttributes) {
        rentalService.cancelRentalRequest(id);
        redirectAttributes.addFlashAttribute("success", "Заявка отменена");
        log.info("Request to POST /rental/{}/cancel", id);
        return "redirect:/rental/my?tab=" + tab;
    }

    @PostMapping("/{id}/return")
    public String initiateRentalReturn(@PathVariable Long id,
                                       @RequestParam(value = "tab", defaultValue = "active") String tab,
                                       RedirectAttributes redirectAttributes) {
        rentalService.initiateRentalReturn(id);
        redirectAttributes.addFlashAttribute("success", "Запрос на возврат отправлен");
        log.info("Request to POST /rental/{}/return", id);
        return "redirect:/rental/my?tab=" + tab;
    }


    @GetMapping("/owner/dashboard")
    public String getOwnerDashboardPage(@RequestParam(value = "pendingPage", defaultValue = "0") int pendingPage,
                                        @RequestParam(value = "activePage", defaultValue = "0") int activePage,
                                        @RequestParam(value = "returnPage", defaultValue = "0") int returnPage,
                                        @RequestParam(value = "completedPage", defaultValue = "0") int completedPage,
                                        @RequestParam(value = "tab", defaultValue = "pending") String tab,
                                        Model model) {
        model.addAttribute("pendingRequests", rentalService.getPendingRequestsForOwner(pendingPage, pageSize));
        model.addAttribute("activeRentals", rentalService.getActiveRentalsForOwner(activePage, pageSize));
        model.addAttribute("pendingReturns", rentalService.getPendingReturnsForOwner(returnPage, pageSize));
        model.addAttribute("completedRentals", rentalService.getCompletedRentalsForOwner(completedPage, pageSize));
        model.addAttribute("currentTab", tab);
        log.info("Request to GET owner/dashboard page");
        return "rentals-owner-dashboard-page";
    }

    @PostMapping("/owner/{id}/confirm")
    public String confirmRentalRequest(@PathVariable Long id,
                                       RedirectAttributes redirectAttributes) {
        rentalService.confirmRentalRequest(id);
        redirectAttributes.addFlashAttribute("success", "Аренда подтверждена");
        log.info("Request to POST /rental/{}/confirm", id);
        return "redirect:/rental/owner/dashboard";
    }

    @PostMapping("/owner/{id}/reject")
    public String rejectRentalRequest(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        rentalService.rejectRentalRequest(id);
        redirectAttributes.addFlashAttribute("success", "Заявка отклонена владельцем");
        log.info("Request to POST /rental/{}/reject", id);
        return "redirect:/rental/owner/dashboard";
    }

    @PostMapping("/owner/{id}/confirm-return")
    public String confirmRentalReturn(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        rentalService.confirmRentalReturn(id);
        redirectAttributes.addAttribute("tab", "returns");
        redirectAttributes.addFlashAttribute("success", "Возврат подтверждён");
        log.info("Request to POST /rental/{}/confirm-return", id);
        return "redirect:/rental/owner/dashboard";
    }

}