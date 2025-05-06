Write-Host "============================"
Write-Host "Compiling SHARED classes..."
Write-Host "============================"
cd "C:\IntroToJava\JavaFinalProject\Monopoly\shared"
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java -Path src).FullName

Write-Host "============================"
Write-Host "Compiling SERVER classes..."
Write-Host "============================"
cd "C:\IntroToJava\JavaFinalProject\Monopoly\server"
javac -encoding UTF-8 -cp "../shared/out;../libs/sqlite-jdbc-3.43.2.0.jar;../libs/slf4j-api-2.0.9.jar;../libs/slf4j-simple-2.0.9.jar" -d out (Get-ChildItem -Recurse -Filter *.java -Path src).FullName

Write-Host "============================"
Write-Host "Compiling CLIENT classes..."
Write-Host "============================"
cd "C:\IntroToJava\JavaFinalProject\Monopoly\client"
javac -encoding UTF-8 -cp "../shared/out" -d out (Get-ChildItem -Recurse -Filter *.java -Path src).FullName

Write-Host "✅ All done!"
