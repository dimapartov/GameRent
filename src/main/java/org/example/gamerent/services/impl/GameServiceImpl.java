package org.example.gamerent.services.impl;

import org.example.gamerent.models.Game;
import org.example.gamerent.repos.GameRepository;
import org.example.gamerent.services.GameService;
import org.example.gamerent.web.viewmodels.GameViewModel;
import org.example.gamerent.web.viewmodels.user_input.GameCreationInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public GameCreationInputModel createGame(GameCreationInputModel newGame) {
        // Преобразуем входную модель в сущность Game
        Game game = modelMapper.map(newGame, Game.class);
        // Гарантируем, что идентификатор равен null, чтобы Hibernate создал новую запись
        game.setId(null);
        // Сохраняем новую игру в базе
        game = gameRepository.save(game);
        // Возвращаем модель создания на основе сохранённой сущности
        return modelMapper.map(game, GameCreationInputModel.class);
    }


    @Override
    public GameViewModel getGameById(Long id) {
        Game game = gameRepository.findById(id).orElse(null);
        return game != null ? modelMapper.map(game, GameViewModel.class) : null;
    }

    @Override
    public List<GameViewModel> getAllGames() {
        List<Game> games = gameRepository.findAll();
        List<GameViewModel> result = new ArrayList<>();
        for (Game game : games) {
            result.add(modelMapper.map(game, GameViewModel.class));
        }
        return result;
    }

    @Override
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    @Override
    public List<GameViewModel> getGamesByName(String name) {
        List<Game> games = gameRepository.findByNameContainingIgnoreCase(name);
        List<GameViewModel> result = new ArrayList<>();
        for (Game game : games) {
            result.add(modelMapper.map(game, GameViewModel.class));
        }
        return result;
    }
}