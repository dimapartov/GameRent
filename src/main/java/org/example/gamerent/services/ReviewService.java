package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.example.gamerent.web.viewmodels.user_input.ReviewInputModel;
import org.springframework.data.domain.Page;


public interface ReviewService {

    ReviewViewModel createReview(ReviewInputModel input);

    void deleteReview(Long reviewId);

    Page<ReviewViewModel> getReviewsAboutUser(String username, String sortBy, int pageNumber, int pageSize);

    Page<ReviewViewModel> getReviewsByUser(String username, String sortBy, int pageNumber, int pageSize);

    Double getAverageRating(String username);

}