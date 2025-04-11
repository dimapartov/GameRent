package org.example.gamerent.repos;

import org.example.gamerent.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // Поиск игр по названию
    List<Game> findByNameContainingIgnoreCase(String name);
}