package org.example.gamerent.repos;

import org.example.gamerent.models.Offer;
import org.example.gamerent.models.consts.OfferDifficulty;
import org.example.gamerent.models.consts.OfferGenre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM Offer o " +
            "WHERE (:priceFrom IS NULL OR o.price >= :priceFrom)" +
            "AND (:priceTo IS NULL OR o.price <= :priceTo)" +
            "AND (:brand IS NULL OR :brand = '' OR LOWER(o.brand.name) = LOWER(:brand))" +
            "AND (:myOffers = false OR o.owner.username = :username)" +
            "AND (:genres IS NULL OR o.genre IN :genres)" +
            "AND (:difficulties IS NULL OR o.difficulty IN :difficulties)"
    )
    Page<Offer> findFilteredOffers(
            @Param("priceFrom") BigDecimal priceFrom,
            @Param("priceTo") BigDecimal priceTo,
            @Param("brand") String brand,
            @Param("myOffers") boolean myOffers,
            @Param("username") String username,
            Pageable pageable,
            @Param("genres") List<OfferGenre> genres,
            @Param("difficulties") List<OfferDifficulty> difficulties
    );

}