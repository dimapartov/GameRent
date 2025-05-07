package org.example.gamerent.services.impl;

import org.example.gamerent.models.Review;
import org.example.gamerent.models.User;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.ReviewRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.ReviewService;
import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.example.gamerent.web.viewmodels.user_input.ReviewInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, RentalRepository rentalRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public ReviewViewModel createReview(ReviewInputModel reviewInputModel) {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (currentUserUsername.equals(reviewInputModel.getRevieweeUsername())) {
            throw new RuntimeException("Нельзя оставить отзыв самому себе");
        }

        User reviewer = userRepository.findUserByUsername(currentUserUsername).orElseThrow(() -> new RuntimeException("Ревьюер не найден"));
        User reviewee = userRepository.findUserByUsername(reviewInputModel.getRevieweeUsername()).orElseThrow(() -> new RuntimeException("Пользователь для отзыва не найден"));

        Review review = modelMapper.map(reviewInputModel, Review.class);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review = reviewRepository.save(review);

        ReviewViewModel reviewViewModel = modelMapper.map(review, ReviewViewModel.class);
        reviewViewModel.setUsername(reviewer.getUsername());
        reviewViewModel.setFullName(reviewer.getFirstName() + " " + reviewer.getLastName());

        return reviewViewModel;
    }

    @Override
    public void deleteReviewById(Long id) {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        if (!review.getReviewer().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Нельзя удалить чужой отзыв");
        }
        reviewRepository.delete(review);
    }

    @Override
    public Page<ReviewViewModel> getReviewsAboutUser(String revieweeUsername, String sortBy, int pageNumber, int pageSize) {
        Sort springSort = buildSpringSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, springSort);
        return reviewRepository.findAllByRevieweeUsername(revieweeUsername, pageable).map(review -> {
            ReviewViewModel reviewViewModel = modelMapper.map(review, ReviewViewModel.class);
            reviewViewModel.setUsername(review.getReviewer().getUsername());
            return reviewViewModel;
        });
    }

    @Override
    public Page<ReviewViewModel> getReviewsByUser(String reviewerUsername, String sortBy, int pageNumber, int pageSize) {
        Sort springSort = buildSpringSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, springSort);
        return reviewRepository.findAllByReviewerUsername(reviewerUsername, pageable).map(review -> {
            ReviewViewModel reviewViewModel = modelMapper.map(review, ReviewViewModel.class);
            reviewViewModel.setUsername(review.getReviewee().getUsername());
            return reviewViewModel;
        });
    }

    @Override
    public Double getUserAverageRating(String revieweeUsername) {
        return reviewRepository.findAverageRatingByRevieweeUsername(revieweeUsername);
    }


    private Sort buildSpringSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "created");
        }
        return switch (sortBy) {
            case "ratingDesc" -> Sort.by(Sort.Direction.DESC, "rating");
            case "ratingAsc" -> Sort.by(Sort.Direction.ASC, "rating");
            case "dateAsc" -> Sort.by(Sort.Direction.ASC, "created");
            case "dateDesc" -> Sort.by(Sort.Direction.DESC, "created");
            default -> Sort.by(Sort.Direction.DESC, "created");
        };
    }

}