# JavaFinalProject

File/Class Folder Purpose
MonopolyUI.java client/src/views/ UI that shows board, handles input
ClientConnection.java client/src/network/ Manages socket connection to server
GameServer.java server/src/app/ Listens for connections, game master
GameState.java shared/src/game/ Holds current board/player data
Player.java shared/src/player/ Player model (shared across both sides)

For windows:

cd server
javac -cp "../shared/out" -d out (Get-ChildItem -Recurse -Filter \*.java -Path src).FullName

java -cp "out;../shared/out" monopoly.server.GameServer

switch to new terminal windows

cd client
javac -cp "../shared/out" -d out (Get-ChildItem -Recurse -Filter \*.java -Path src).FullName

java -cp "out;../shared/out" monopoly.client.MonopolyGUI localhost 5100

$javaFiles = Get-ChildItem -Recurse -Filter \*.java -Path src | ForEach-Object { $\_.FullName }

> > javac -cp ../shared/out -d out $javaFiles

shared

javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter \*.java -Path src).FullName
