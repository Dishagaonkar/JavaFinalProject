# Launch Monopoly Server
Write-Host "Launching Monopoly Server..."
Start-Process powershell -ArgumentList '-NoExit', '-Command', @"
cd '\server'
java -cp 'out;../shared/out;../libs/sqlite-jdbc-3.43.2.0.jar;../libs/slf4j-api-2.0.9.jar;../libs/slf4j-simple-2.0.9.jar' monopoly.server.GameServer
"@


Start-Sleep -Seconds 2


Write-Host "Launching Monopoly Client..."
Start-Process powershell -ArgumentList '-NoExit', '-Command', @"
cd '..\client'
java -cp 'out;../shared/out' monopoly.client.MonopolyGUI
"@


Start-Process powershell -ArgumentList '-NoExit', '-Command', @"
cd 'C:\IntroToJava\JavaFinalProject\Monopoly\client'
java -cp 'out;../shared/out' monopoly.client.MonopolyGUI
"@

