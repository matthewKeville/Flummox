package com.keville.ReBoggled.service.view;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.GameUserViewDTO;
import com.keville.ReBoggled.DTO.PostGameUserViewDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.game.ScoreBoardEntry;
import com.keville.ReBoggled.model.game.UserGameBoardWord;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.answerService.AnswerService;
import com.keville.ReBoggled.service.answerService.AnswerServiceException;
import com.keville.ReBoggled.service.gameService.GameService;
import com.keville.ReBoggled.service.gameService.GameServiceException;
import com.keville.ReBoggled.service.userService.UserService;
import com.keville.ReBoggled.service.view.GameViewServiceException.GameViewServiceError;

@Component
public class GameViewService {

    private static final Logger LOG = LoggerFactory.getLogger(GameViewService.class);

    private GameService gameService;
    private UserService userService;
    private AnswerService answerService;

    public GameViewService(
        @Autowired GameService gameService,
        @Autowired UserService userService,
        @Autowired AnswerService answerService) {
      this.gameService = gameService;
      this.userService = userService;
      this.answerService = answerService;
    }

    /* Return a view of a game to the perspective of a user, when the game is ongoing */
    public GameUserViewDTO getGameUserViewDTO(Integer gameId,Integer userId) throws GameViewServiceException,GameServiceException {

      Game game = gameService.getGame(gameId);
      User user = userService.getUser(userId);

      //extract users answers 
      Set<GameAnswer> userAnswers = game.answers.stream().filter( ans -> {
        return ans.user.getId().equals(userId);
      }).collect(Collectors.toSet());

      return new GameUserViewDTO(game,userAnswers);
    }

    /* Return a view of a game to the perspective of a user, when the game is complete */
    public PostGameUserViewDTO getPostGameUserViewDTO(Integer gameId,Integer userId) throws GameViewServiceException {

      try {

        Game game = gameService.getGame(gameId);
        User user = userService.getUser(userId);
        Set<UserGameBoardWord> userGameBoardWords = answerService.getUserGameBoardWords(game, user);
        List<ScoreBoardEntry> scoreBoard = answerService.getScoreBoard(game);

        return new PostGameUserViewDTO(game, userGameBoardWords, scoreBoard);

      } catch (GameServiceException | AnswerServiceException ex) {
        LOG.error(String.format("unable to get PostGameUserViewDTO for game %d and user %d",gameId,userId));
        throw new GameViewServiceException(GameViewServiceError.ERROR);
      }

    }


}
