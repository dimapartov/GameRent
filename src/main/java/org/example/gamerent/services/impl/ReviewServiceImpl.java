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
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             RentalRepository rentalRepository,
                             ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public ReviewViewModel createReview(ReviewInputModel newReviewInputModel) {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (currentUserUsername.equals(newReviewInputModel.getRevieweeUsername())) {
            throw new RuntimeException("Нельзя оставить отзыв самому себе");
        }

        User reviewerModel = userRepository.findUserByUsername(currentUserUsername).orElseThrow(() -> new RuntimeException("Ревьюер не найден"));
        User revieweeModel = userRepository.findUserByUsername(newReviewInputModel.getRevieweeUsername()).orElseThrow(() -> new RuntimeException("Пользователь для отзыва не найден"));

        Review reviewModel = modelMapper.map(newReviewInputModel, Review.class);
        reviewModel.setReviewer(reviewerModel);
        reviewModel.setReviewee(revieweeModel);
        reviewModel = reviewRepository.save(reviewModel);

        ReviewViewModel reviewViewModel = modelMapper.map(reviewModel, ReviewViewModel.class);
        reviewViewModel.setUsername(reviewerModel.getUsername());
        reviewViewModel.setFullName(reviewerModel.getFirstName() + " " + reviewerModel.getLastName());

        return reviewViewModel;
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Review reviewModel = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        if (!reviewModel.getReviewer().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Нельзя удалить чужой отзыв");
        }
        reviewRepository.delete(reviewModel);
    }

    @Override
    public Page<ReviewViewModel> getReviewsAboutUser(String revieweeUsername, String sortBy, int pageNumber, int pageSize) {
        Sort springSort = buildSpringSort(sortBy);

        Pageable pageSettings = PageRequest.of(pageNumber, pageSize, springSort);

        return reviewRepository.findAllByRevieweeUsername(revieweeUsername, pageSettings).map(review -> {
            ReviewViewModel reviewViewModel = modelMapper.map(review, ReviewViewModel.class);
            reviewViewModel.setUsername(review.getReviewer().getUsername());
            return reviewViewModel;
        });
    }

    @Override
    public Page<ReviewViewModel> getReviewsByUser(String reviewerUsername, String sortBy, int pageNumber, int pageSize) {
        Sort springSort = buildSpringSort(sortBy);

        Pageable pageSettings = PageRequest.of(pageNumber, pageSize, springSort);

        return reviewRepository.findAllByReviewerUsername(reviewerUsername, pageSettings).map(review -> {
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