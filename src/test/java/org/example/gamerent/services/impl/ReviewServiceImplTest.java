package org.example.gamerent.services.impl;

import org.example.gamerent.models.Review;
import org.example.gamerent.models.User;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.ReviewRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.example.gamerent.web.viewmodels.user_input.ReviewInputModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    private static final String CURRENT_USER = "alice";
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ReviewServiceImpl reviewService;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setUsername(CURRENT_USER);
        alice.setFirstName("Alice");
        alice.setLastName("Anderson");
        bob = new User();
        bob.setUsername("bob");

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(CURRENT_USER, null));
    }

    @Test
    void createReview_success() {
        // Arrange
        ReviewInputModel input = new ReviewInputModel();
        input.setRevieweeUsername("bob");
        input.setRating(5);
        input.setText("Great experience");

        when(userRepository.findUserByUsername(CURRENT_USER))
                .thenReturn(Optional.of(alice));
        when(userRepository.findUserByUsername("bob"))
                .thenReturn(Optional.of(bob));
        when(modelMapper.map(input, Review.class))
                .thenAnswer(invocation -> new Review());
        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ReviewViewModel vmStub = new ReviewViewModel();
        when(modelMapper.map(any(Review.class), eq(ReviewViewModel.class)))
                .thenReturn(vmStub);

        // Act
        ReviewViewModel result = reviewService.createReview(input);

        // Assert
        assertSame(vmStub, result);
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        Review saved = captor.getValue();
        assertEquals(alice, saved.getReviewer());
        assertEquals(bob, saved.getReviewee());
        assertEquals(CURRENT_USER, result.getUsername());
        assertEquals("Alice Anderson", result.getFullName());
    }

    @Test
    void createReview_selfReview_throws() {
        ReviewInputModel input = new ReviewInputModel();
        input.setRevieweeUsername(CURRENT_USER);
        input.setRating(3);
        input.setText("Self review");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reviewService.createReview(input)
        );
        assertEquals("Нельзя оставить отзыв самому себе", ex.getMessage());
    }

    @Test
    void createReview_reviewerNotFound_throws() {
        when(userRepository.findUserByUsername(CURRENT_USER))
                .thenReturn(Optional.empty());
        ReviewInputModel input = new ReviewInputModel();
        input.setRevieweeUsername("bob");
        input.setRating(4);
        input.setText("Nice");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reviewService.createReview(input)
        );
        assertEquals("Ревьюер не найден", ex.getMessage());
    }

    @Test
    void createReview_revieweeNotFound_throws() {
        when(userRepository.findUserByUsername(CURRENT_USER))
                .thenReturn(Optional.of(alice));
        when(userRepository.findUserByUsername("bob"))
                .thenReturn(Optional.empty());

        ReviewInputModel input = new ReviewInputModel();
        input.setRevieweeUsername("bob");
        input.setRating(4);
        input.setText("Nice");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reviewService.createReview(input)
        );
        assertEquals("Пользователь для отзыва не найден", ex.getMessage());
    }

    @Test
    void deleteReviewById_success() {
        Review review = new Review();
        review.setReviewer(alice);
        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        reviewService.deleteReviewById(1L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReviewById_notFound_throws() {
        when(reviewRepository.findById(2L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reviewService.deleteReviewById(2L)
        );
        assertEquals("Отзыв не найден", ex.getMessage());
    }

    @Test
    void deleteReviewById_notOwner_throws() {
        Review review = new Review();
        User other = new User();
        other.setUsername("charlie");
        review.setReviewer(other);
        when(reviewRepository.findById(3L))
                .thenReturn(Optional.of(review));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reviewService.deleteReviewById(3L)
        );
        assertEquals("Нельзя удалить чужой отзыв", ex.getMessage());
    }

    @Test
    void getReviewsAboutUser_pagesCorrectly() {
        Review r = new Review();
        r.setReviewer(alice);
        r.setReviewee(bob);
        Page<Review> page = new PageImpl<>(List.of(r), PageRequest.of(0, 2), 1);
        when(reviewRepository.findAllByRevieweeUsername(
                eq("bob"), any(PageRequest.class)
        )).thenReturn(page);
        when(modelMapper.map(r, ReviewViewModel.class)).thenAnswer(inv -> {
            ReviewViewModel vm = new ReviewViewModel();
            vm.setUsername(alice.getUsername());
            return vm;
        });

        var result = reviewService.getReviewsAboutUser("bob", "ratingDesc", 0, 2);
        assertEquals(1, result.getContent().size());
        assertEquals(alice.getUsername(), result.getContent().get(0).getUsername());
    }

    @Test
    void getReviewsByUser_pagesCorrectly() {
        Review r = new Review();
        r.setReviewer(alice);
        r.setReviewee(bob);
        Page<Review> page = new PageImpl<>(List.of(r), PageRequest.of(1, 1), 1);
        when(reviewRepository.findAllByReviewerUsername(
                eq(CURRENT_USER), any(PageRequest.class)
        )).thenReturn(page);
        when(modelMapper.map(r, ReviewViewModel.class)).thenAnswer(inv -> {
            ReviewViewModel vm = new ReviewViewModel();
            vm.setUsername(bob.getUsername());
            return vm;
        });

        var result = reviewService.getReviewsByUser(CURRENT_USER, "dateAsc", 1, 1);
        assertEquals(1, result.getContent().size());
        assertEquals(bob.getUsername(), result.getContent().get(0).getUsername());
    }

    @Test
    void getUserAverageRating_delegates() {
        when(reviewRepository.findAverageRatingByRevieweeUsername("bob"))
                .thenReturn(4.2);

        Double avg = reviewService.getUserAverageRating("bob");
        assertEquals(4.2, avg);
    }

}