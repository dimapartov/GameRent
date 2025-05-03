package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.example.gamerent.web.viewmodels.user_input.ReviewInputModel;
import org.springframework.data.domain.Page;


public interface ReviewService {

    ReviewViewModel createReview(ReviewInputModel reviewInputModel);

    void deleteReviewById(Long id);

    Page<ReviewViewModel> getReviewsAboutUser(String revieweeUsername, String sortBy, int pageNumber, int pageSize);

    Page<ReviewViewModel> getReviewsByUser(String reviewerUsername, String sortBy, int pageNumber, int pageSize);

    Double getUserAverageRating(String revieweeUsername);

}