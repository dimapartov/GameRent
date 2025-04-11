package org.example.gamerent.repos;

import org.example.gamerent.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Получить отзывы, оставленные пользователем
    List<Review> findByReviewer_Id(Long reviewerId);
    // Получить отзывы, полученные пользователем
    List<Review> findByReviewee_Id(Long revieweeId);
}