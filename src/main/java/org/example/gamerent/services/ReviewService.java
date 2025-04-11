package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.ReviewViewModel;

import java.util.List;

public interface ReviewService {
    ReviewViewModel createReview(ReviewViewModel reviewVM);
    ReviewViewModel getReviewById(Long id);
    List<ReviewViewModel> getAllReviews();
    void deleteReview(Long id);

    ReviewViewModel leaveReview(Long reviewerId, Long revieweeId, String text, Integer rating);
}