package org.example.gamerent.web.controllers;

import jakarta.validation.Valid;
import org.example.gamerent.services.RentalService;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public String requestRental(@ModelAttribute("rentalInput") @Valid RentalRequestInputModel input, BindingResult errors, RedirectAttributes ra
    ) {
        if (errors.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalInput", errors);
            ra.addFlashAttribute("rentalInput", input);
            return "redirect:/offer/" + input.getOfferId();
        }
        try {
            rentalService.createRentalRequest(input);
            ra.addFlashAttribute("success", "Заявка отправлена");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/offer/" + input.getOfferId();
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.cancelRentalRequest(id);
        ra.addFlashAttribute("success", "Заявка отменена");
        return "redirect:/rentals/my";
    }

    @PostMapping("/{id}/return")
    public String initiateReturn(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.initiateReturn(id);
        ra.addFlashAttribute("success", "Запрос на возврат отправлен");
        return "redirect:/rentals/my";
    }

    @GetMapping("/my")
    public String myRentals(Model model) {
        model.addAttribute("rentals", rentalService.getMyRentals());
        return "rental-my-page";
    }

}