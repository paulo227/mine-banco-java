@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set MAVEN_HOME=%USERPROFILE%\apache-maven-3.9.16
set PATH=%MAVEN_HOME%\bin;%PATH%

echo.
echo  ███╗   ███╗██╗███╗   ██╗███████╗██████╗  █████╗ ███╗   ██╗ ██████╗ ██████╗
echo  ████╗ ████║██║████╗  ██║██╔════╝██╔══██╗██╔══██╗████╗  ██║██╔════╝██╔═══██╗
echo  ██╔████╔██║██║██╔██╗ ██║█████╗  ██████╔╝███████║██╔██╗ ██║██║     ██║   ██║
echo  ██║╚██╔╝██║██║██║╚██╗██║██╔══╝  ██╔══██╗██╔══██║██║╚██╗██║██║     ██║   ██║
echo  ██║ ╚═╝ ██║██║██║ ╚████║███████╗██████╔╝██║  ██║██║ ╚████║╚██████╗╚██████╔╝
echo  ╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚══════╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝
echo.

if /i "%1"=="spring" (
    echo === Iniciando Spring Boot (API) ===
    echo Acesse a API em:    http://localhost:8081/api/contas
    echo Acesse o Swagger em: http://localhost:8081/swagger-ui.html
    mvn spring-boot:run

) else if /i "%1"=="test" (
    echo === Executando Testes JUnit 5 ===
    mvn test

) else if /i "%1"=="frontend" (
    echo === Iniciando Frontend Angular ===
    cd frontend
    npm run start

) else if /i "%1"=="build" (
    echo === Build completo (back-end + testes) ===
    mvn clean package

) else (
    echo Uso: executar.bat [comando]
    echo.
    echo Comandos disponíveis:
    echo   spring    - Inicia o servidor Spring Boot (porta 8081)
    echo   test      - Executa todos os testes JUnit 5 com relatório
    echo   frontend  - Inicia o frontend Angular (porta 4200)
    echo   build     - Compila e empacota o projeto
    echo.
    echo Variáveis de ambiente opcionais:
    echo   DB_URL      - URL do banco  (padrão: jdbc:postgresql://localhost:5432/minebanco)
    echo   DB_USER     - Usuário do BD (padrão: postgres)
    echo   DB_PASSWORD - Senha do BD   (padrão: postgres)
)
