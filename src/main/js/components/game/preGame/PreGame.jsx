import React, { useState } from 'react';
import {  useRouteLoaderData, useNavigate, useRevalidator } from "react-router-dom";
import { toast } from 'react-toastify';
import { Stack ,Button,Group,Text,ActionIcon } from '@mantine/core';
import { IconTrash, IconMessage, IconUserShare, IconDoorExit, IconPlayerPlay, IconAdjustments } from '@tabler/icons-react';

import Chat from "/src/main/js/components/game/preGame/Chat.jsx";
import PlayerList from "/src/main/js/components/game/preGame/playerList/PlayerList.jsx";
import GameSettings from "/src/main/js/components/game/preGame/GameSettings.jsx";

import { GetInviteLink, StartLobby, LeaveLobby, DeleteLobby } from "/src/main/js/services/LobbyService.ts";
import { CopyToClipboardInsecure } from "/src/main/js/services/ClipboardService.ts";

export default function PreGame({lobby,playedPrev,onReturnToPostGame}) {

  const navigate = useNavigate();
  const revalidator = useRevalidator();
  const { userInfo } = useRouteLoaderData("root");
  const [showSettings, setShowSettings]  = useState(false)

  const isOwner = (lobby.owner.id == userInfo.id);

  let onFinishSettingsView = function() {
    setShowSettings(false);
  }

  let leaveLobby = async function(lobbyId) {

    let serviceResponse = await LeaveLobby(lobbyId);

    if ( serviceResponse.success ) {
      revalidator.revalidate()
      navigate("/lobby");
      return
    } 

    toast.error(serviceResponse.errorMessage);

  }

  let copyInviteLink = async function() {

    let serviceResponse = await GetInviteLink()

    if ( !serviceResponse.success ) {

      toast.error(serviceResponse.errorMessage);
      return;

    } else {

      var lobbyInviteLink = serviceResponse.data
      CopyToClipboardInsecure(lobbyInviteLink)
      toast.info("Invite Copied To Clipboard")

    }
  }

  let deleteLobby = async function(lobbyId) {

    let serviceResponse = await DeleteLobby(lobbyId)

    if ( !serviceResponse.success ) {

      toast.error(serviceResponse.errorMessage);
      return;

    } else {

      revalidator.revalidate()
      navigate("/lobby")

    }
  }

  let startLobby = async function(lobbyId) {

    let serviceResponse = await StartLobby(lobbyId)

    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage);
      return;
    } 
  }

  let getLobbyButtons = function() {

    let buttons = []
    buttons.push(
      <Button color="yellow" onClick={() => copyInviteLink()}>
        <IconUserShare/>
      </Button>)
    buttons.push(
      <Button color="orange" onClick={() => {setShowSettings(true)}}>
        <IconAdjustments/>
      </Button>)

    if (isOwner) {
      buttons.push(
        <Button color="green" onClick={() => startLobby(lobby.id)}>
          <IconPlayerPlay/>
        </Button>)
      buttons.push(
        <Button color="red" onClick={() => deleteLobby(lobby.id)} >
          <IconTrash/>
        </Button>)
    } else {
      buttons.push(
        <Button color="red" onClick={() => leaveLobby(lobby.id)} >
          <IconDoorExit/>
        </Button>)
    }

    if (playedPrev) {
      buttons.push(<Button color="grape" onClick={onReturnToPostGame}>Last</Button>)
    }

    return ( <Button.Group>{buttons}</Button.Group> )
  }

  if ( !lobby ) {
    return (<></>)
  }

  return (
    <>

      <Stack align="center" justify="flex-start">

        <Text style={{ textAlign: "center" }}>Welcome to {lobby.name}</Text>

        {!showSettings
          ? <Chat lobby={lobby}/> 
          : <GameSettings lobby={lobby} onFinish={onFinishSettingsView}/>
        }

        <Group justify="center">
          {getLobbyButtons()}
        </Group>

      </Stack>

    </>
  )

}
