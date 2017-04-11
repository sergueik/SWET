@echo OFF

if "%JAVA_VERSION%"=="" set JAVA_VERSION=1.8.0_101
if "%JAVA_HOME%"=="" set JAVA_HOME=c:\java\jdk%JAVA_VERSION%
set JAVA_OPTS=-Xms256m -Xmx512m

if "%MAVEN_VERSION%"=="" set MAVEN_VERSION=3.3.9
if "%M2_HOME%"=="" set M2_HOME=c:\java\apache-maven-%MAVEN_VERSION%
if "%M2%"=="" set M2=%M2_HOME%\bin
set MAVEN_OPTS=-Xms256m -Xmx512m

PATH=%JAVA_HOME%\bin;%M2%;%PATH%

set TARGET=%CD%\target

set PACKAGE_NAME=swet
set PACKAGE_VERSION=0.0.5-SNAPSHOT
set MAIN_APP_PACKAGE=org.swet
set MAIN_APP_CLASS=%1
if "%MAIN_APP_CLASS%"=="" set MAIN_APP_CLASS=SimpleToolBarEx

call mvn package install

java -cp %TARGET%\%PACKAGE_NAME%-%PACKAGE_VERSION%.jar;%TARGET%\lib\* ^
%MAIN_APP_PACKAGE%.%MAIN_APP_CLASS%

goto :EOF