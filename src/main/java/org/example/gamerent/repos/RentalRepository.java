package org.example.gamerent.repos;

import org.example.gamerent.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

}