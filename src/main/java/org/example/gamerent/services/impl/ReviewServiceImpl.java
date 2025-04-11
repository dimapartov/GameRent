package org.example.gamerent.services.impl;

import org.example.gamerent.models.Review;
import org.example.gamerent.repos.ReviewRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.ReviewService;
import org.example.gamerent.services.dto.ReviewDTO;
import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ReviewViewModel createReview(ReviewViewModel reviewVM) {
        Review review = modelMapper.map(reviewVM, Review.class);
        review = reviewRepository.save(review);
        return modelMapper.map(review, ReviewViewModel.class);
    }

    @Override
    public ReviewViewModel getReviewById(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        return review != null ? modelMapper.map(review, ReviewViewModel.class) : null;
    }

    @Override
    public List<ReviewViewModel> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewViewModel> result = new ArrayList<>();
        for (Review review : reviews) {
            result.add(modelMapper.map(review, ReviewViewModel.class));
        }
        return result;
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public ReviewViewModel leaveReview(Long reviewerId, Long revieweeId, String text, Integer rating) {
        // Создаем DTO для отзыва
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewerId(reviewerId);
        reviewDTO.setRevieweeId(revieweeId);
        reviewDTO.setText(text);
        reviewDTO.setRating(rating);

        // Мапим DTO в сущность Review посредством ModelMapper
        Review review = modelMapper.map(reviewDTO, Review.class);

        // Если требуется, можно вручную корректно установить ассоциации:
        review.setReviewer(userRepository.findById(reviewerId).orElse(null));
        review.setReviewee(userRepository.findById(revieweeId).orElse(null));

        review = reviewRepository.save(review);
        return modelMapper.map(review, ReviewViewModel.class);
    }
}