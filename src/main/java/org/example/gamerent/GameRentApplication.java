package org.example.gamerent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class GameRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameRentApplication.class, args);
        System.out.println("Приложение готово к работе");
    }

}