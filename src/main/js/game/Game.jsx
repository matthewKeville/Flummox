import React, { useState, useRef, useEffect } from 'react';
import { toast } from 'react-toastify';

import GameTimer from "./GameTimer.jsx";
import Board from "./Board.jsx";
import AnswerDisplay from './AnswerDisplay.jsx';
import WordInput from './WordInput.jsx';

export async function loader({params}) {
  const lobbyId = params.lobbyId
  return  { lobbyId };
}

export default function Game({lobby,onGameEnd}) {

  const [game,setGame] = useState(null)

  useEffect(() => {

    console.log("in game use effect ")

    const fetchInitialGame = async () => {

      const response = await fetch("/api/game/"+lobby.gameId+"/view/user");
      let initialGame = await response.json()

      console.log("loaded inital game")
      console.log(initialGame)

      setGame(initialGame)
    }

    fetchInitialGame()

    console.log("setting up game source")
    const evtSource = new EventSource("/api/game/"+lobby.gameId+"/view/user/sse")

    evtSource.addEventListener("game_change", (e) => {
      console.log("game change recieved");
      let newGameData = JSON.parse(e.data)
      setGame(newGameData)
      console.log(newGameData)
    });

    return () => {
      console.log("closing the game event source") 
      evtSource.close()
    };

  }, []);

  //  We need useEffect to load inital data ...
  if (lobby == null) {
    return
  }

  async function onSubmitAnswer(word) {

    let answerBody = {}
    answerBody.answer = word

    const response = await fetch("/api/game/"+lobby.gameId+"/answer", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(answerBody)
    });

    if ( response.status == 200 ) {

      toast.success("nice");

    } else if ( response.url == authenticateUrl ) {

      toast.error("incorrect");

    } else {
    
      const content  = await response.json();
      console.log(`unable to update lobby because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }
  }

  if ( game == null ) {
    return
  }

  return (
    <>
      <div className="game-flex">
        <GameTimer gameEnd={lobby.gameEnd} onGameEnd={onGameEnd}/>
        <Board dice={game.gameViewDTO.boardString.split("")} />
        <WordInput onWordInput={onSubmitAnswer}/>
        <AnswerDisplay words={game.answers.map( answer => answer.answer)} />
      </div>
    </>
  );

}



