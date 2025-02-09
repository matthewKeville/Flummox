package com.keville.flummox.model.game;

import java.time.LocalDateTime;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;

import com.keville.flummox.model.user.User;

public class GameAnswer {

  @Column("user")
  public AggregateReference<User, Integer> user;
  public String answer;

  @Column("ANSWER_SUBMISSION_TIME")
  public LocalDateTime answerSubmissionTime;

  public  GameAnswer() {}
  public  GameAnswer(Integer userId,String answer) {
    this.user = AggregateReference.to(userId);
    this.answer = answer;
    this.answerSubmissionTime = LocalDateTime.now();
  }
}
