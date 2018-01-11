@echo OFF
:setlocal
if "%TOOLS_DIR%"=="" set TOOLS_DIR=c:\java
if "%JAVA_VERSION%"=="" set JAVA_VERSION=1.8.0_101
if "%JAVA_HOME%"=="" set JAVA_HOME=%TOOLS_DIR%\jdk%JAVA_VERSION%
set JAVA_OPTS=-Xms256m -Xmx512m
if "%MAVEN_VERSION%"=="" set MAVEN_VERSION=3.3.9
if "%M2_HOME%"=="" set M2_HOME=%TOOLS_DIR%\apache-maven-%MAVEN_VERSION%
if "%M2%"=="" set M2=%M2_HOME%\bin
set MAVEN_OPTS=-Xms256m -Xmx512m
PATH=%JAVA_HOME%\bin;%M2%;%PATH%
set TARGET=%CD%\target
set PACKAGE_NAME=swet
set PACKAGE_VERSION=0.0.8-SNAPSHOT
set MAIN_APP_PACKAGE=com.github.sergueik.swet
set MAIN_APP_CLASS=%1
if NOT "%MAIN_APP_CLASS%" == "" shift
if "%MAIN_APP_CLASS%"=="" set MAIN_APP_CLASS=SimpleToolBarEx
set APP_HOME=%CD:\=/%

REM compile
call mvn -Dmaven.test.skip=true package install

REM run
REM NOTE: shift does not affect the value of %* 
java -Dlog4j.configuration=file:///%APP_HOME%/src/main/resources/log4j.xml -cp %TARGET%\%PACKAGE_NAME%-%PACKAGE_VERSION%.jar;%TARGET%\lib\* ^
%MAIN_APP_PACKAGE%.%MAIN_APP_CLASS% %1 %2 %3 %4 %5 %6 %7 %8 %9
:endlocal
goto :EOF
