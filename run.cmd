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
set VERBOSE=true

set APP_NAME=swet
set APP_VERSION=0.0.9-SNAPSHOT
set PACKAGE=com.github.sergueik.swet
set DEFAULT_MAIN_CLASS=SimpleToolBarEx

call :CALL_JAVASCRIPT /project/artifactId
set ARTIFACTID=%VALUE%

call :CALL_JAVASCRIPT /project/groupId
set GROUPID=%VALUE%

call :CALL_JAVASCRIPT /project/version
set VERSION=%VALUE%

call :CALL_JAVASCRIPT /project/properties/mainClass
set DEFAULT_MAIN_CLASS=%VALUE%

if /i NOT "%VERBOSE%"=="true" goto :CONTINUE

echo APP_VERSION="%APP_VERSION%">&2
echo APP_NAME="%APP_NAME%">&2
echo PACKAGE="%PACKAGE%">&2
echo DEFAULT_MAIN_CLASS="%DEFAULT_MAIN_CLASS%">&2

:CONTINUE

set MAIN_CLASS=%1
if NOT "%MAIN_CLASS%" == "" shift
if "%MAIN_CLASS%"=="" set MAIN_CLASS=%DEFAULT_MAIN_CLASS%
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
REM NOTE: shift does not modify %*
REM The log4j configuration argument seems to be ignored
REM -Dlog4j.configuration=file:///%APP_HOME%/src/main/resources/log4j.properties ^
set COMMAND=^
java ^
  -cp %TARGET%\%APP_NAME%-%APP_VERSION%.jar;%TARGET%\lib\* ^
  %PACKAGE%.%MAIN_CLASS% ^
  %1 %2 %3 %4 %5 %6 %7 %8 %9
echo %COMMAND%>&2
%COMMAND%
ENDLOCAL
exit /b

:CALL_JAVASCRIPT

REM This script extracts project g.a.v a custom property from pom.xml using mshta.exe and DOM selectSingleNode method
set "SCRIPT=mshta.exe "javascript:{"
set "SCRIPT=%SCRIPT% var fso = new ActiveXObject('Scripting.FileSystemObject');"
set "SCRIPT=%SCRIPT% var out = fso.GetStandardStream(1);"
set "SCRIPT=%SCRIPT% var fh = fso.OpenTextFile('pom.xml', 1, true);"
set "SCRIPT=%SCRIPT% var xd = new ActiveXObject('Msxml2.DOMDocument');"
set "SCRIPT=%SCRIPT% xd.async = false;"
set "SCRIPT=%SCRIPT% data = fh.ReadAll();"
set "SCRIPT=%SCRIPT% xd.loadXML(data);"
set "SCRIPT=%SCRIPT% root = xd.documentElement;"
set "SCRIPT=%SCRIPT% var xpath = '%~1';"
set "SCRIPT=%SCRIPT% var xmlnode = root.selectSingleNode( xpath);"
set "SCRIPT=%SCRIPT% if (xmlnode != null) {"
set "SCRIPT=%SCRIPT%   out.Write(xpath + '=' + xmlnode.text);"
set "SCRIPT=%SCRIPT% } else {"
set "SCRIPT=%SCRIPT%   out.Write('ERR');"
set "SCRIPT=%SCRIPT% }"
set "SCRIPT=%SCRIPT% close();}""

for /F "tokens=2 delims==" %%_ in ('%SCRIPT% 1 ^| more') do set VALUE=%%_
ENDLOCAL
exit /b
