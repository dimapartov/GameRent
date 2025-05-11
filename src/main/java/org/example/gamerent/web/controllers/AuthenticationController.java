package org.example.gamerent.web.controllers;

import jakarta.validation.Valid;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/user")
public class AuthenticationController {

    private final RegistrationService registrationService;


    @Autowired
    public AuthenticationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    @ModelAttribute("newUser")
    public RegistrationInputModel initRegistrationInputModel() {
        return new RegistrationInputModel();
    }


    @GetMapping("/login")
    public String getLoginPage() {
        return "user-login-page";
    }

    @PostMapping("/login-error")
    public String redirectOnFailedLogin(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("badCredentials", true);
        return "redirect:/user/login";
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "user-registration-page";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("newUser") RegistrationInputModel newUser,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("newUser", newUser);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "newUser", bindingResult);
            return "redirect:/user/register";
        }
        registrationService.registerUser(newUser);
        return "redirect:/user/login";
    }

}