package org.example.gamerent.repos;

import org.example.gamerent.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    // Поиск офферов по названию игры
    List<Offer> findByGame_NameContainingIgnoreCase(String gameName);
    // Поиск офферов по названию бренда (через игру.brand)
    List<Offer> findByGame_Brand_NameContainingIgnoreCase(String brandName);
    // Фильтрация офферов по цене (например, между minPrice и maxPrice)
    List<Offer> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    // Фильтрация по срокам аренды (например, используя дополнительное поле minRentalDays, если оно добавлено в Offer)
    List<Offer> findByMinRentalDaysLessThanEqual(Integer days);
}