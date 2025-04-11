package org.example.gamerent.repos;

import org.example.gamerent.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    // Метод для поиска брендов по названию (если потребуется)
    List<Brand> findByNameContainingIgnoreCase(String name);
}