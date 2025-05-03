package org.example.gamerent.web.controllers;

import org.example.gamerent.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/owner/rentals")
public class OwnerRentalController {

    private final RentalService rentalService;

    @Autowired
    public OwnerRentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/dashboard")
    public String getRentalOwnerDashboardPage(Model model) {
        model.addAttribute("pendingRequests", rentalService.getPendingRequestsForOwner());
        model.addAttribute("activeRentals", rentalService.getActiveRentalsForOwner());
        model.addAttribute("pendingReturns", rentalService.getPendingReturnsForOwner());
        return "rental-owner-dashboard-page";
    }

    @PostMapping("/{id}/confirm")
    public String confirmRental(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.confirmRentalRequest(id);
        ra.addFlashAttribute("success", "Аренда подтверждена");
        return "redirect:/owner/rentals/dashboard";
    }


    @PostMapping("/{id}/reject")
    public String rejectRental(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.rejectRentalRequest(id);
        ra.addFlashAttribute("success", "Заявка отклонена владельцем");
        return "redirect:/owner/rentals/dashboard";
    }

    @PostMapping("/{id}/confirm-return")
    public String confirmReturn(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.confirmRentalReturn(id);
        ra.addFlashAttribute("success", "Возврат подтверждён");
        return "redirect:/owner/rentals/dashboard";
    }

}