# compile-all.ps1
# ----------------
# (1) Make all paths relative to this script:
$root = $PSScriptRoot

# ── SHARED ───────────────────────────────────────────────────────────────────
Write-Host "============================"
Write-Host "Compiling SHARED classes..."
Write-Host "============================"
Push-Location "$root/shared"

# create out folder if needed
if (!(Test-Path out)) { New-Item -ItemType Directory out | Out-Null }

# grab all .java under src
$sharedSources = Get-ChildItem -Recurse -Filter '*.java' -Path src |
                  Select-Object -ExpandProperty FullName

# compile
javac -encoding UTF-8 -d out $sharedSources

Pop-Location


# ── SERVER ───────────────────────────────────────────────────────────────────
Write-Host "============================"
Write-Host "Compiling SERVER classes..."
Write-Host "============================"
Push-Location "$root/server"

if (!(Test-Path out)) { New-Item -ItemType Directory out | Out-Null }

$serverSources = Get-ChildItem -Recurse -Filter '*.java' -Path src |
                  Select-Object -ExpandProperty FullName

# NOTE: on macOS/Linux, classpath entries are separated by “:”
$cp = "$root/shared/out" +
      ":" + "$root/libs/sqlite-jdbc-3.43.2.0.jar" +
      ":" + "$root/libs/slf4j-api-2.0.9.jar" +
      ":" + "$root/libs/slf4j-simple-2.0.9.jar"

javac -encoding UTF-8 -cp $cp -d out $serverSources

Pop-Location


# ── CLIENT ───────────────────────────────────────────────────────────────────
Write-Host "============================"
Write-Host "Compiling CLIENT classes..."
Write-Host "============================"
Push-Location "$root/client"

if (!(Test-Path out)) { New-Item -ItemType Directory out | Out-Null }

$clientSources = Get-ChildItem -Recurse -Filter '*.java' -Path src |
                  Select-Object -ExpandProperty FullName

# Only needs shared classes on its classpath:
$cpClient = "$root/shared/out"

javac -encoding UTF-8 -cp $cpClient -d out $clientSources

Pop-Location


Write-Host "✅ All done!"
