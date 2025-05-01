package org.example.gamerent.repos;

import org.example.gamerent.models.Rental;
import org.example.gamerent.models.consts.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByOfferOwnerUsernameAndStatus(String ownerUsername, RentalStatus status);

    List<Rental> findByRenterUsername(String renterUsername);

    List<Rental> findAllByStatusAndCreatedBefore(RentalStatus status, LocalDateTime cutoff);

    boolean existsByRenterUsernameAndOfferOwnerIdAndEndDateBefore(
            String renterUsername,
            Long ownerId,
            LocalDateTime now
    );

}