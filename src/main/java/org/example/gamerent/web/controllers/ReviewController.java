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

    private final ReviewService reviewService;

    @Value("${review.page.size}")
    private int pageSize;


    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping
    public String myReviews(
            @ModelAttribute("filters") ReviewFiltersDTO filters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
            Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Page<ReviewViewModel> aboutPage = reviewService.getReviewsAboutUser(currentUser, sortBy, page, pageSize);
        Page<ReviewViewModel> byPage = reviewService.getReviewsByUser(currentUser, sortBy, page, pageSize);

        model.addAttribute("aboutPage", aboutPage);
        model.addAttribute("byPage", byPage);
        model.addAttribute("avgRating", reviewService.getAverageRating(currentUser));
        return "reviews";
    }

    @GetMapping("/about/{username}")
    public String reviewsAbout(
            @PathVariable("username") String revieweeUsername,
            @ModelAttribute("filters") ReviewFiltersDTO filters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
            Model model
    ) {
        Page<ReviewViewModel> reviewsPage = reviewService.getReviewsAboutUser(revieweeUsername, sortBy, page, pageSize);
        model.addAttribute("reviewsPage", reviewsPage);
        model.addAttribute("avgRating", reviewService.getAverageRating(revieweeUsername));
        ReviewInputModel newReview = new ReviewInputModel();
        newReview.setRevieweeUsername(revieweeUsername);
        model.addAttribute("newReview", newReview);
        model.addAttribute("revieweeUsername", revieweeUsername);
        return "reviews-about";
    }

    @PostMapping("/about/{username}")
    public String postReview(
            @PathVariable("username") String revieweeUsername,
            @Valid @ModelAttribute("newReview") ReviewInputModel input,
            BindingResult errors,
            @ModelAttribute("filters") ReviewFiltersDTO filters,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
            Model model
    ) {
        if (errors.hasErrors()) {
            return reviewsAbout(revieweeUsername, filters, page, sortBy, model);
        }
        reviewService.createReview(input);
        return "redirect:/reviews/about/" + revieweeUsername + "?page=" + page + "&sortBy=" + sortBy;
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "") String sortBy
    ) {
        reviewService.deleteReview(id);
        return "redirect:/reviews?page=" + page + "&sortBy=" + sortBy;
    }

}