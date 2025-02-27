package com.keville.flummox.model.game;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;

@Configurable
public class Game {

  @Transient
  private static final Logger LOG = LoggerFactory.getLogger(Game.class);

  @Id
  public Integer id;

  @Column("GAME_START")
  public LocalDateTime start;

  @Column("GAME_END")
  public LocalDateTime end;

  @MappedCollection(idColumn = "GAME")
  public Set<GameUserReference> users = new HashSet<GameUserReference>();

  @MappedCollection(idColumn = "GAME")
  public Set<GameAnswer> answers = new HashSet<GameAnswer>();

  @LastModifiedDate
  @Column("LAST_MODIFIED")
  public LocalDateTime lastModifiedDate;

  //FIXME these are game settings and should be a nested class
  public FindRule findRule;
  public Integer duration;

  @Embedded.Nullable
  public Board board;

  public  Game() {}

}
