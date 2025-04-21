# JavaFinalProject

File/Class	Folder	Purpose
MonopolyUI.java	client/src/views/	UI that shows board, handles input
ClientConnection.java	client/src/network/	Manages socket connection to server
GameServer.java	server/src/app/	Listens for connections, game master
GameState.java	shared/src/game/	Holds current board/player data
Player.java	shared/src/player/	Player model (shared across both sides)