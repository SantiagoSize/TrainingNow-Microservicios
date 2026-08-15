@echo off
setlocal

REM ============================================================
REM  Limpia las cuentas de prueba sueltas en trainingnow_usuarios
REM  (no forman parte del roster final de 3 usuarios, 3 entrenadores
REM 
 y 2 admins). Borra primero sus relaciones entrenador-cliente
REM  y despues las cuentas. Pide confirmacion antes de tocar nada.
REM ============================================================

set DB=trainingnow_usuarios
set USUARIO=root
set MYSQL=

REM 1) Si "mysql" ya esta en el PATH (ej. terminal de Laragon), usarlo tal cual.
where mysql >nul 2>nul
if not errorlevel 1 set MYSQL=mysql

REM 2) Si no, buscar el mysql.exe que trae Laragon empacado, en las ubicaciones
REM    tipicas (C:\laragon y D:\laragon, cualquier version instalada).
if "%MYSQL%"=="" (
    for %%D in (C D) do (
        for /d %%V in ("%%D:\laragon\bin\mysql\mysql-*") do (
            if exist "%%V\bin\mysql.exe" set MYSQL="%%V\bin\mysql.exe"
        )
    )
)

if "%MYSQL%"=="" (
    echo No se encontro mysql.exe ni en el PATH ni en C:\laragon o D:\laragon.
    echo Abre este .bat desde la Terminal de Laragon, o edita la variable MYSQL
    echo al inicio de este archivo con la ruta exacta a tu mysql.exe.
    pause
    exit /b 1
)

echo Usando: %MYSQL%

echo Se van a eliminar estas cuentas de prueba de %DB%:
echo   - camila.herrera@gmail.com
echo   - sas@gmail.com
echo   - Gonzalo@gmail.com
echo   - Juana@gmail.com
echo   - juanito@gmail.com
echo.
set /p CONFIRMAR="Escribe S y Enter para continuar (cualquier otra cosa cancela): "
if /i not "%CONFIRMAR%"=="S" (
    echo Cancelado, no se borro nada.
    pause
    exit /b 0
)

%MYSQL% -u %USUARIO% %DB% -e "DELETE FROM trainer_clients WHERE client_id IN (SELECT id FROM users WHERE email IN ('camila.herrera@gmail.com','sas@gmail.com','Gonzalo@gmail.com','Juana@gmail.com','juanito@gmail.com')) OR trainer_id IN (SELECT id FROM users WHERE email IN ('camila.herrera@gmail.com','sas@gmail.com','Gonzalo@gmail.com','Juana@gmail.com','juanito@gmail.com')); DELETE FROM users WHERE email IN ('camila.herrera@gmail.com','sas@gmail.com','Gonzalo@gmail.com','Juana@gmail.com','juanito@gmail.com');"

if errorlevel 1 (
    echo.
    echo Algo fallo al ejecutar el DELETE. Revisa el mensaje de arriba.
) else (
    echo.
    echo Listo. Cuentas de prueba eliminadas.
    echo Reinicia TrainNow-Usuarios para que repararNombresBase corrija los apellidos base.
)
pause
