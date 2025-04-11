package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.GameViewModel;

import java.util.List;

public interface GameService {
    GameViewModel createGame(GameViewModel gameVM);
    GameViewModel getGameById(Long id);
    List<GameViewModel> getAllGames();
    void deleteGame(Long id);

    List<GameViewModel> getGamesByName(String name);
}