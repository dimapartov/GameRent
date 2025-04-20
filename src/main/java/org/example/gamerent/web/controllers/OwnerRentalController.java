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

    @GetMapping
    public String requests(Model model) {
        model.addAttribute("requests", rentalService.getOwnerRequests());
        return "rental-owner-requests-page";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.confirmRental(id);
        ra.addFlashAttribute("success", "Аренда подтверждена");
        return "redirect:/owner/rentals";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.cancelRequest(id);
        ra.addFlashAttribute("success", "Заявка отклонена");
        return "redirect:/owner/rentals";
    }

}