package org.example.gamerent.repos;

import org.example.gamerent.models.Rental;
import org.example.gamerent.models.consts.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Page<Rental> findByOfferOwnerUsernameAndStatus(String ownerUsername, RentalStatus status, Pageable pageable);

    List<Rental> findAllByStatusAndCreatedBefore(RentalStatus status, LocalDateTime cutoff);

    Page<Rental> findByRenterUsernameAndStatus(String renterUsername, RentalStatus status, Pageable pageable);

    boolean existsByRenterUsernameAndOfferOwnerIdAndEndDateBefore(
            String renterUsername,
            Long ownerId,
            LocalDateTime now
    );

}