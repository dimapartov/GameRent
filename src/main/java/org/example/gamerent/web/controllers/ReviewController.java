package org.example.gamerent.web.controllers;

import jakarta.validation.Valid;
import org.example.gamerent.services.ReviewService;
import org.example.gamerent.services.dto.ReviewFiltersDTO;
import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.example.gamerent.web.viewmodels.user_input.ReviewInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Value("${reviews.page.size}")
    private int pageSize;

    private final ReviewService reviewService;


    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping("/my")
    public String getMyReviewsPage(@ModelAttribute("filters") ReviewFiltersDTO filters,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                   Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Page<ReviewViewModel> byPage = reviewService.getReviewsByUser(currentUser, sortBy, page, pageSize);
        model.addAttribute("byPage", byPage);
        return "reviews-my-page";
    }


    @GetMapping("/about/{username}")
    public String getReviewsAboutPage(@PathVariable("username") String revieweeUsername,
                                      @ModelAttribute("filters") ReviewFiltersDTO filters,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                      Model model) {
        Page<ReviewViewModel> reviewsPage = reviewService.getReviewsAboutUser(revieweeUsername, sortBy, page, pageSize);
        model.addAttribute("reviewsPage", reviewsPage);
        model.addAttribute("avgRating", reviewService.getUserAverageRating(revieweeUsername));
        if (!model.containsAttribute("newReview")) {
            ReviewInputModel newReview = new ReviewInputModel();
            newReview.setRevieweeUsername(revieweeUsername);
            model.addAttribute("newReview", newReview);
        }
        model.addAttribute("revieweeUsername", revieweeUsername);
        return "reviews-about-user-page";
    }

    @PostMapping("/about/{username}")
    public String postReview(@PathVariable("username") String revieweeUsername,
                             @Valid @ModelAttribute("newReview") ReviewInputModel input,
                             BindingResult bindingResult,
                             @ModelAttribute("filters") ReviewFiltersDTO filters,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                             Model model) {
        if (bindingResult.hasErrors()) {
            Page<ReviewViewModel> reviewsPage =
                    reviewService.getReviewsAboutUser(revieweeUsername, sortBy, page, pageSize);
            model.addAttribute("reviewsPage", reviewsPage);
            model.addAttribute("avgRating", reviewService.getUserAverageRating(revieweeUsername));
            model.addAttribute("revieweeUsername", revieweeUsername);
            return "reviews-about-user-page";
        }
        reviewService.createReview(input);
        return "redirect:/reviews/about/" + revieweeUsername + "?page=" + page + "&sortBy=" + sortBy;
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id,
                               @RequestParam(value="revieweeUsername", required=false) String revieweeUsername,
                               @RequestParam(value="page", defaultValue="0") int page,
                               @RequestParam(value="sortBy", defaultValue="") String sortBy) {
        reviewService.deleteReviewById(id);

        if (revieweeUsername != null && !revieweeUsername.isBlank()) {
            // back to the “about user” page we came from
            return "redirect:/reviews/about/" + revieweeUsername + "?page=" + page + "&sortBy=" + sortBy;
        }

        // otherwise, default to your own reviews
        return "redirect:/reviews/my?page=" + page + "&sortBy=" + sortBy;
    }


}