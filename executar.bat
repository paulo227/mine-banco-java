@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set MAVEN_HOME=%USERPROFILE%\apache-maven-3.9.16
set PATH=%MAVEN_HOME%\bin;%PATH%

if /i "%1"=="spring" (
    echo === Iniciando Spring Boot (API + Frontend) ===
    echo Acesse em: http://localhost:8081
    mvn spring-boot:run
) else if /i "%1"=="test" (
    echo === Executando Testes ===
    mvn compile -q
    set CP=target\classes
    set CP=!CP!;%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.7.4\postgresql-42.7.4.jar
    set CP=!CP!;%USERPROFILE%\.m2\repository\com\zaxxer\HikariCP\5.1.0\HikariCP-5.1.0.jar
    java -cp "!CP!" test.TesteMineBanco
) else (
    echo === Compilando ===
    mvn compile -q
    set CP=target\classes
    set CP=!CP!;%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.7.4\postgresql-42.7.4.jar
    set CP=!CP!;%USERPROFILE%\.m2\repository\com\zaxxer\HikariCP\5.1.0\HikariCP-5.1.0.jar
    java -cp "!CP!" app.Main
)
