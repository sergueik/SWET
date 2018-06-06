@echo OFF
SETLOCAL

set SKIP_TEST=tue
if "%TOOLS_DIR%"=="" set TOOLS_DIR=c:\java
if "%JAVA_VERSION%"=="" set JAVA_VERSION=1.8.0_101
if "%JAVA_HOME%"=="" set JAVA_HOME=%TOOLS_DIR%\jdk%JAVA_VERSION%
set JAVA_OPTS=-Xms256m -Xmx512m
if "%MAVEN_VERSION%"=="" set MAVEN_VERSION=3.5.0
if "%M2_HOME%"=="" set M2_HOME=%TOOLS_DIR%\apache-maven-%MAVEN_VERSION%
if "%M2%"=="" set M2=%M2_HOME%\bin
set MAVEN_OPTS=-Xms256m -Xmx512m
PATH=%JAVA_HOME%\bin;%M2%;%PATH%
set TARGET=%CD%\target
set APP_NAME=swet
set APP_VERSION=0.0.9-SNAPSHOT
set PACKAGE=com.github.sergueik.swet

call :CALL_JAVASCRIPT groupId
set PACKAGE=%VALUE%

call :CALL_JAVASCRIPT artifactId
set APP_NAME=%VALUE%
call :CALL_JAVASCRIPT version
set APP_VERSION=%VALUE%

if /i NOT "%VERBOSE%"=="true" goto :CONTINUE

echo APP_VERSION="%APP_VERSION%"
echo APP_NAME="%APP_NAME%"
echo PACKAGE="%PACKAGE%"

:CONTINUE


set MAIN_CLASS=%1
if NOT "%MAIN_CLASS%" == "" shift
if "%MAIN_CLASS%"=="" set MAIN_CLASS=SimpleToolBarEx
set APP_HOME=%CD:\=/%
REM omit the extension - on different Windows
REM will be mvn.bat or mvn.cmd

if "%SKIP_TEST%"=="" (
REM Test
call mvn test
REM Compile
call mvn package install
) else (
REM compile
call mvn -Dmaven.test.skip=true package install
)

REM Run
REM NOTE: shift does not affect the value of %*
REM passsing the log4j configuration seems to be have no effect
REM  -Dlog4j.configuration=file:///%APP_HOME%/src/main/resources/log4j.properties ^
echo ON
java ^
  -cp %TARGET%\%APP_NAME%-%APP_VERSION%.jar;%TARGET%\lib\* ^
  %PACKAGE%.%MAIN_CLASS% ^
  %1 %2 %3 %4 %5 %6 %7 %8 %9
@echo OFF
ENDLOCAL
exit /b

:CALL_JAVASCRIPT

set "SCRIPT=mshta.exe "javascript:{"
set "SCRIPT=%SCRIPT% var fso = new ActiveXObject('Scripting.FileSystemObject');"
set "SCRIPT=%SCRIPT% var out = fso.GetStandardStream(1);"
set "SCRIPT=%SCRIPT% var handle = fso.OpenTextFile('pom.xml',1,1);"
set "SCRIPT=%SCRIPT% var xml = new ActiveXObject('Msxml2.DOMDocument.6.0');"
set "SCRIPT=%SCRIPT% xml.async = false;"
set "SCRIPT=%SCRIPT% xml.loadXML(handle.ReadAll());"
set "SCRIPT=%SCRIPT% root = xml.documentElement;"
set "SCRIPT=%SCRIPT% var tag ='%~1';"
set "SCRIPT=%SCRIPT% nodes = root.childNodes;"
set "SCRIPT=%SCRIPT% for(i = 0; i != nodes .length; i++){"
set "SCRIPT=%SCRIPT%   if (nodes.item(i).nodeName.match(RegExp(tag, 'g'))) {"
set "SCRIPT=%SCRIPT%     out.Write(tag + '=' + nodes.item(i).text + '\n');"
set "SCRIPT=%SCRIPT%   }"
set "SCRIPT=%SCRIPT% }"
set "SCRIPT=%SCRIPT%close();}""

REM if /i "%DEBUG%"=="true" echo %SCRIPT%
REM if /i "%DEBUG%"=="true" for /F "delims=" %%_ in ('%SCRIPT% 1 ^| more') do echo %%_

for /F "tokens=2 delims==" %%_ in ('%SCRIPT% 1 ^| more') do set VALUE=%%_
ENDLOCAL
exit /b
