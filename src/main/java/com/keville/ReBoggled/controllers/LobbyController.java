package com.keville.ReBoggled.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.keville.ReBoggled.DTO.JoinLobbyResponseDTO;
import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.service.LobbyService;
import com.keville.ReBoggled.service.UserService;
import com.keville.ReBoggled.util.Conversions;

import jakarta.servlet.http.HttpSession;

@RestController
public class LobbyController {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyController.class);

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private UserService userService;

    public LobbyController(@Autowired LobbyService lobbies) {
      this.lobbyService = lobbies;
    }

    @GetMapping(value = {"/lobby", "/"})
    public ModelAndView view() {
      //return the lobby template 
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("lobby");
      return modelAndView;
    }

    private LobbyDTO lobbyToLobbyDTO(Lobby lobby) {

      LobbyDTO lobbyDto = new LobbyDTO(lobby);
      User owner = userService.getUser(lobby.owner.getId());

      List<Integer> userIds = lobby.users.stream()
        .map( x -> x.user.getId() )
        .collect(Collectors.toList());
      List<User> users = userService.getUsers(userIds);

      List<LobbyUserDTO> userDtos = users.stream().
        map( x -> new LobbyUserDTO(x))
        .collect(Collectors.toList());

      lobbyDto.owner = new LobbyUserDTO(owner);
      lobbyDto.users = userDtos;

      return lobbyDto;
    }

    @GetMapping("/api/lobby")
    //public Iterable<Lobby> test(
    public Iterable<LobbyDTO> getLobbies(
        @RequestParam(required = false, name = "publicOnly") boolean publicOnly,
        HttpSession session) {

      LOG.info("hit /api/lobby");

      Iterable<Lobby> lobbies = lobbyService.getLobbies();
      List<LobbyDTO> lobbyDTOs = Conversions.iterableToList(lobbies).stream()
        .map( lobby -> lobbyToLobbyDTO(lobby ))
        .collect(Collectors.toList());

      return lobbyDTOs;
    }

    @GetMapping("/api/lobby/{id}")
    public LobbyDTO getLobby(
        @PathVariable("id") Integer id,
        @Autowired HttpSession session) {

      LOG.info("hit /api/lobby/" + id);
      Optional<Lobby> lobby = lobbyService.getLobby(id);

      if (!lobby.isPresent()) {
        LOG.warn("attempted to access a non-existant lobby " + id);
        return null;
      } 

      return lobbyToLobbyDTO(lobby.get());

    }

    @PostMapping("/api/lobby/{id}/join")
    public Object joinLobby(
        @PathVariable("id") Integer id,
        @Autowired HttpSession session) {

      LOG.info("hit /api/lobby/"+id+"/leave");
      Integer userId = (Integer) session.getAttribute("userId");

      LOG.info(String.format("from session with userId : %d",userId));

      // Is the lobby private ?
      Optional<Lobby> optLobby = lobbyService.getLobby(id);
      if (!optLobby.isPresent()) {
        LOG.warn(String.format("User %d attempted to join a non-existant lobby %d",userId,id));
        return new JoinLobbyResponseDTO(false,"lobby not found");
      }
      Lobby lobby = optLobby.get();

      if ( lobby.isPrivate ) {
        LOG.warn(String.format("User %d attempted to join a private lobby %d",userId,id));
        return new JoinLobbyResponseDTO(false,"lobby is private");
      }

      if ( lobby.users.size() == lobby.capacity ) {
        LOG.warn(String.format("User %d attempted to join lobby %d but it's at capacity",userId,id));
        return new JoinLobbyResponseDTO(false,"lobby is at capacity");
      }

      lobbyService.addUserToLobby(userId, id);
      return new JoinLobbyResponseDTO(true,"");

    }


    @PostMapping("/api/lobby/{id}/leave")
    public void leaveLobby(
        @PathVariable("id") Integer id,
        @Autowired HttpSession session) {

      LOG.info("hit /api/lobby/"+id+"/leave");

      Integer userId = (Integer) session.getAttribute("userId");

      lobbyService.removeUserFromLobby(userId,id);
    }

}
