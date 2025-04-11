package org.example.gamerent.repos;

import org.example.gamerent.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // можно добавить кастомные методы поиска по username, email и т.п.
}