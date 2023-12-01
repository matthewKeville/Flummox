package com.keville.ReBoggled.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.UserRepository;


@Component
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private UserRepository users;

    public UserService(@Autowired UserRepository users) {
      this.users = users;
    }

    public Iterable<User> getUsers() {
      return users.findAll();
    }

    public User getUser(int id) {
      return users.findById(id).get();
    }

    public List<User> getUsers(List<Integer> ids) {
      List<User> foundUsers = new LinkedList<User>();
      for ( Integer id : ids ) {
        Optional<User> user = users.findById(id);
        if ( !user.isEmpty() ) {
          foundUsers.add(user.get());
        } else {
          LOG.warn("unable to locate user with ID : " + id);
        }
      }
      return foundUsers;
    }

    public void addLobby(User user) {
      users.save(user);
    }
}
