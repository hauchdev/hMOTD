# Build script for hMOTD on Windows
$ErrorActionPreference = "Stop"

$javaHome = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Users\junio\.jdks\ms-21.0.12.1" }
$mavenPath = if (Get-Command mvn -ErrorAction SilentlyContinue) { "mvn" } else { "$PSScriptRoot\.maven\apache-maven-3.10.0\bin\mvn.cmd" }

$env:JAVA_HOME = $javaHome
& $mavenPath clean package
