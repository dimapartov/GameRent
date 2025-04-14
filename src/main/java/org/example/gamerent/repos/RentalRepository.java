package org.example.gamerent.repos;

import org.example.gamerent.models.Rental;
import org.example.gamerent.models.consts.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // Получить активные аренды пользователя
    List<Rental> findByRenter_IdAndStatus(Long renterId, RentalStatus status);

    // Получить аренды по офферу
    List<Rental> findByOffer_Id(Long offerId);

}