package com.keville.flummox.service.gameService.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.flummox.model.game.Board;
import com.keville.flummox.model.game.BoardSize;
import com.keville.flummox.model.game.BoardTopology;
import com.keville.flummox.model.game.Tile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BoardGenerator {

  private static Logger LOG = LoggerFactory.getLogger(BoardGenerator.class);

  private ClassicTilesGenerator classicTilesGenerator;

  public BoardGenerator(@Autowired ClassicTilesGenerator classicTilesGenerator) {
    this.classicTilesGenerator = classicTilesGenerator;
  }

  public Board generate(BoardSize size,BoardTopology topology,boolean tileRotation) throws BoardGenerationException {

    //In the future we delegate (important for tileset and mutation)
    LOG.debug(" defaulting to classic tile generation ");
    List<Tile> tiles = classicTilesGenerator.generate(size,tileRotation);

    Board board = new Board(size,topology,tiles,tileRotation);
    return board;
    
  }

  public Board generateFromTileString(String tileString,BoardSize size,BoardTopology topology) throws BoardGenerationException {

    List<Tile> tiles = classicTilesGenerator.generateFromTileString(tileString);
    Board board = new Board(size,topology,tiles,false);

    return board;

  }

}
