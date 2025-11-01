@echo off
echo ============================================
echo Demarrage Wildfly Local sur port 8180
echo ============================================

REM Tuer les processus Wildfly existants
taskkill /F /IM java.exe /FI "WINDOWTITLE eq Wildfly*" 2>nul

REM Attendre 2 secondes
timeout /t 2 /nobreak >nul

REM Nettoyer les deployments
del /Q %WILDFLY_HOME%\standalone\deployments\*.war.* 2>nul

REM Copier les nouveaux WARs
echo Copie des WARs...
copy /Y app2-multiplication\target\app2-multiplication.war %WILDFLY_HOME%\standalone\deployments\
copy /Y app3-interface\target\app3-interface.war %WILDFLY_HOME%\standalone\deployments\

REM Demarrer Wildfly avec offset
echo Demarrage de Wildfly sur port 8180...
cd %WILDFLY_HOME%\bin
start "Wildfly Local 8180" standalone.bat -Djboss.socket.binding.port-offset=100

echo ============================================
echo Wildfly demarre sur http://localhost:8180
echo App2: http://localhost:8180/app2-multiplication/multiplication
echo App3: http://localhost:8180/app3-interface/
echo ============================================