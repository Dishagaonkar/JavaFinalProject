# Launch Monopoly Server
Write-Host "🚀 Launching Monopoly Server..."
Start-Process powershell -ArgumentList '-NoExit', '-Command', @"
cd 'C:\IntroToJava\JavaFinalProject\Monopoly\server'
java -cp 'out;../shared/out;../libs/sqlite-jdbc-3.43.2.0.jar;../libs/slf4j-api-2.0.9.jar;../libs/slf4j-simple-2.0.9.jar' monopoly.server.GameServer
"@

# Wait for the server to initialize
Start-Sleep -Seconds 2

# Launch Monopoly Client
Write-Host "🎮 Launching Monopoly Client..."
Start-Process powershell -ArgumentList '-NoExit', '-Command', @"
cd 'C:\IntroToJava\JavaFinalProject\Monopoly\client'
java -cp 'out;../shared/out' monopoly.client.MonopolyGUI
"@
