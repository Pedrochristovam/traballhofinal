@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MVNW_VERBOSE=false

set BASEDIR=%~dp0
@REM %~dp0 sempre vem com barra no final. Essa barra antes da aspas final
@REM pode quebrar o parsing do java.exe. Então a gente remove.
if "%BASEDIR:~-1%"=="\" set BASEDIR=%BASEDIR:~0,-1%

set WRAPPER_JAR=%BASEDIR%\.mvn\wrapper\maven-wrapper.jar
set WRAPPER_PROPERTIES=%BASEDIR%\.mvn\wrapper\maven-wrapper.properties

if not exist "%WRAPPER_JAR%" (
  echo Maven Wrapper JAR not found: "%WRAPPER_JAR%"
  echo Please ensure .mvn/wrapper/maven-wrapper.jar exists.
  exit /b 1
)

if exist "%JAVA_HOME%\bin\java.exe" (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXE=java
)

"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%BASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

endlocal
