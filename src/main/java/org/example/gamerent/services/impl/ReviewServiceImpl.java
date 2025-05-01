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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;
    private final ModelMapper mapper;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepo,
                             UserRepository userRepo,
                             RentalRepository rentalRepo,
                             ModelMapper mapper) {
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public ReviewViewModel createReview(ReviewInputModel input) {
        String reviewerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (reviewerUsername.equals(input.getRevieweeUsername()))
            throw new IllegalArgumentException("Нельзя оставить отзыв самому себе");
        User reviewee = userRepo.findUserByUsername(input.getRevieweeUsername()).orElseThrow(() -> new IllegalArgumentException("Пользователь для отзыва не найден"));
        User reviewer = userRepo.findUserByUsername(reviewerUsername).orElseThrow(() -> new IllegalArgumentException("Ревьюер не найден"));
        Review entity = mapper.map(input, Review.class);
        entity.setReviewer(reviewer);
        entity.setReviewee(reviewee);
        entity = reviewRepo.save(entity);
        ReviewViewModel vm = mapper.map(entity, ReviewViewModel.class);
        vm.setReviewerUsername(reviewer.getUsername());
        vm.setReviewerFullName(reviewer.getFirstName() + " " + reviewer.getLastName());
        return vm;
    }


    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        String reviewerUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Отзыв не найден"));
        if (!r.getReviewer().getUsername().equals(reviewerUsername)) {
            throw new SecurityException("Нельзя удалить чужой отзыв.");
        }
        reviewRepo.delete(r);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewViewModel> getReviewsAboutUser(
            String username, String sortBy, int pageNumber, int pageSize
    ) {
        Sort sort = buildSpringSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return reviewRepo.findAllByRevieweeUsername(username, pageable)
                .map(r -> {
                    ReviewViewModel vm = mapper.map(r, ReviewViewModel.class);
                    vm.setReviewerUsername(r.getReviewer().getUsername());
                    return vm;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewViewModel> getReviewsByUser(
            String username, String sortBy, int pageNumber, int pageSize
    ) {
        Sort sort = buildSpringSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return reviewRepo.findAllByReviewerUsername(username, pageable)
                .map(r -> {
                    ReviewViewModel vm = mapper.map(r, ReviewViewModel.class);
                    vm.setReviewerUsername(r.getReviewer().getUsername());
                    return vm;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(String username) {
        return reviewRepo.findAverageRatingByRevieweeUsername(username);
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