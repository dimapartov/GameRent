package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.GameViewModel;
import org.example.gamerent.web.viewmodels.user_input.GameCreationInputModel;

import java.util.List;


public interface GameService {

    GameCreationInputModel createGame(GameCreationInputModel newGame);

    GameViewModel getGameById(Long id);

    List<GameViewModel> getAllGames();

    void deleteGame(Long id);

    List<GameViewModel> getGamesByName(String name);

}