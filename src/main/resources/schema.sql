DROP TABLE IF EXISTS game_answer;
DROP TABLE IF EXISTS tile;
DROP TABLE IF EXISTS lobby_user_reference;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS lobby;
DROP TABLE IF EXISTS game;

-- formerly userinfo
CREATE TABLE IF NOT EXISTS user(

  ID INT AUTO_INCREMENT PRIMARY KEY,

  USERNAME VARCHAR(40) not null,
  PASSWORD VARCHAR(80) not null,
  EMAIL VARCHAR(255),

  VERIFIED BOOLEAN not null,
  GUEST BOOLEAN not null,
  LOBBY INTEGER,
  OWNED_LOBBY INTEGER

);

CREATE TABLE IF NOT EXISTS game(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  GAME_START TIMESTAMP,
  GAME_END TIMESTAMP,
  LAST_MODIFIED TIMESTAMP,
  FIND_RULE VARCHAR(40) NOT NULL,
  DURATION VARCHAR(40) NOT NULL,
  -- GameSettings
  BOARD_SIZE VARCHAR(40) NOT NULL,
  BOARD_TOPOLOGY VARCHAR(40) NOT NULL
); 

CREATE TABLE IF NOT EXISTS game_answer(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USER INT,
  ANSWER VARCHAR(40),
  ANSWER_SUBMISSION_TIME TIMESTAMP,
  GAME INT,
  CONSTRAINT fk_game_answer_game_reference FOREIGN KEY(GAME) REFERENCES game(ID) on DELETE CASCADE,
  CONSTRAINT fk_game_answer_user_reference FOREIGN KEY(USER) REFERENCES user(ID) on DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tile (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  CODE INT,
  ROTATION INT,
  GAME INT,
  GAME_KEY INT,
  CONSTRAINT fk_tile_game_reference FOREIGN KEY(GAME) REFERENCES game(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lobby(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL,
  CAPACITY INT NOT null,
  IS_PRIVATE BOOLEAN NOT NULL,
  -- OWNER INT UNIQUE, (in prod not in test)
  OWNER INT,
  GAME INT,
  LAST_MODIFIED TIMESTAMP,
  -- GameSettings
  BOARD_SIZE VARCHAR(40) NOT NULL,
  BOARD_TOPOLOGY VARCHAR(40) NOT NULL,
  TILE_ROTATION BOOLEAN NOT NULL,
  FIND_RULE VARCHAR(40) NOT NULL,
  DURATION VARCHAR(40) NOT NULL,

  CONSTRAINT fk_lobby_game_reference FOREIGN KEY(GAME) REFERENCES game(ID) ON DELETE SET NULL

); 

CREATE TABLE IF NOT EXISTS lobby_user_reference(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USER INT,
  LOBBY INT,
  UNIQUE (USER),
  CONSTRAINT fk_lobby_user_lobby_reference FOREIGN KEY(LOBBY) REFERENCES lobby(ID) on DELETE CASCADE,
  CONSTRAINT fk_lobby_user_user_reference FOREIGN KEY(USER) REFERENCES user(ID) on DELETE CASCADE
);
