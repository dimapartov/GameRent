package org.example.gamerent.repos;

import org.example.gamerent.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByRevieweeUsername(String username, Pageable pageable);

    Page<Review> findAllByReviewerUsername(String username, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.username = :username")
    Double findAverageRatingByRevieweeUsername(@Param("username") String username);

}