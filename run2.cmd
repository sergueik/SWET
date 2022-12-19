@echo OFF
SETLOCAL

set SKIP_TEST=tue
if "%TOOLS_DIR%"=="" set TOOLS_DIR=c:\java
if "%JAVA_VERSION%"=="" set JAVA_VERSION=1.8.0_101
if "%JAVA_HOME%"=="" set JAVA_HOME=%TOOLS_DIR%\jdk%JAVA_VERSION%
set JAVA_OPTS=-Xms256m -Xmx512m
if "%MAVEN_VERSION%"=="" set MAVEN_VERSION=3.6.1
if "%M2_HOME%"=="" set M2_HOME=%TOOLS_DIR%\apache-maven-%MAVEN_VERSION%
if "%M2%"=="" set M2=%M2_HOME%\bin
set MAVEN_OPTS=-Xms256m -Xmx512m

REM Clear the environment entry that is created by git bash when starting cmd and ruins Maven 3.5.0 ANSI colors.
REM see also: https://issues.apache.org/jira/browse/MNG-6282
REM See also: https://stackoverflow.com/questions/43425304/how-to-customize-colors-in-maven-3-5-console-output
set TERM=

PATH=%JAVA_HOME%\bin;%M2%;%PATH%

set DEBUG=true
set TARGET=target
set VERBOSE=true

set APP_NAME=swet
set APP_VERSION=0.0.10-SNAPSHOT
REM if "%APP_PACKAGE%"=="" set APP_PACKAGE=com.github.sergueik.swet
set DEFAULT_MAIN_CLASS=SimpleToolBarEx
set SCM_CONNECTION=

call :CALL_JAVASCRIPT /project/artifactId VALUE1
set APP_NAME=%VALUE1%

if NOT "%APP_PACKAGE%"=="" goto :app_package_set_explicitly
call :CALL_JAVASCRIPT /project/groupId VALUE2
set APP_PACKAGE=%VALUE2%
:app_package_set_explicitly

call :CALL_JAVASCRIPT /project/version VALUE3
set APP_VERSION=%VALUE3%

call :CALL_JAVASCRIPT /project/properties/mainClass VALUE4
set DEFAULT_MAIN_CLASS=%VALUE4%

call :CALL_JAVASCRIPT /project/scm/connection VALUE5
set SCM_CONNECTION=%VALUE5:scm:git:=%


call :CALL_JAVASCRIPT /project/build/plugins/plugin/configuration/finalName VALUE6
set FINALNAME=%VALUE6:"=%
REM TODO: detect variable interpolation and give up

set APP_JAR=%APP_NAME%.jar

if /i "%SKIP_PACKAGE_VERSION%"=="true" goto :SKIP_PACKAGE_VERSION
set APP_JAR=%APP_NAME%-%APP_VERSION%.jar
:SKIP_PACKAGE_VERSION

REM Testing skipping the default jar
REM origin: https://stackoverflow.com/questions/12809559/remove-jar-created-by-default-in-maven
REM see also: https://www.cyberforum.ru/javafx/thread3059580.html
REM TODO: scan the "pom.xml" for the presence of the "finalName" property (custom configuration)
if NOT "%FINALNAME%" == "" set APP_JAR=%FINALNAME%.jar

if /i NOT "%VERBOSE%"=="true" goto :CONTINUE

call :SHOW_VARIABLE APP_VERSION
call :SHOW_VARIABLE APP_NAME
call :SHOW_VARIABLE APP_PACKAGE
call :SHOW_VARIABLE APP_JAR
call :SHOW_VARIABLE DEFAULT_MAIN_CLASS
call :SHOW_VARIABLE SCM_CONNECTION
call :SHOW_VARIABLE FINALNAME

:CONTINUE

CALL :SHOW_LAST_ARGUMENT %*

set CLEAN=%1

if /i "%CLEAN%" EQU "clean" shift
if /i "%DEBUG%" EQU "true" (
  if /i "%CLEAN%" EQU "clean" echo >&2 CLEAN=%CLEAN%
  if "%ARGS_COUNT%" GEQ "1" echo >&2 ARG1=%1
  if "%ARGS_COUNT%" GEQ "2" echo >&2 ARG2=%2
  if "%ARGS_COUNT%" GEQ "3" echo >&2 ARG3=%3
  if "%ARGS_COUNT%" GEQ "4" echo >&2 ARG4=%4
  if "%ARGS_COUNT%" GEQ "5" echo >&2 ARG5=%5
  if "%ARGS_COUNT%" GEQ "6" echo >&2 ARG6=%6
  if "%ARGS_COUNT%" GEQ "7" echo >&2 ARG7=%7
  if "%ARGS_COUNT%" GEQ "8" echo >&2 ARG8=%8
  if "%ARGS_COUNT%" GEQ "9" echo >&2 ARG9=%9
)

set MAIN_CLASS=%~1
if NOT "%MAIN_CLASS%" == "" shift
if "%MAIN_CLASS%"=="" set MAIN_CLASS=%DEFAULT_MAIN_CLASS%

set APP_HOME=%CD:\=/%
REM omit the extension - on different Windows
REM will be mvn.bat or mvn.cmd

if "%SKIP_TEST%"=="" (
REM Test
call mvn test 2 > NUL
) else (
if /i NOT "%SKIP_BUILD%" == "true" (
REM Compile

if /i "%CLEAN%" EQU "clean" (
  ECHO "DO CLEAN"  
  call mvn -Dmaven.test.skip=true clean package install
) else (
  ECHO "DO INCREMENTAL"  

  call mvn -Dmaven.test.skip=true package install
)
)
)
REM Run
REM NOTE: shift does not modify the %*
REM The log4j configuration argument seems to be ignored
REM -Dlog4j.configuration=file:///%APP_HOME%/src/main/resources/log4j.properties ^
set COMMAND=^
java ^
  -cp %TARGET%\%APP_JAR%;%TARGET%\lib\* ^
  %APP_PACKAGE%.%MAIN_CLASS% ^
  %1 %2 %3 %4 %5 %6 %7 %8 %9
echo %COMMAND%>&2
%COMMAND%
ENDLOCAL
exit /b

:CALL_JAVASCRIPT

REM This script extracts project g.a.v a custom property from pom.xml using mshta.exe and DOM selectSingleNode method
set "SCRIPT=javascript:{"
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
set "SCRIPT=%SCRIPT% close();}"

for /F "tokens=2 delims==" %%_ in ('mshta.exe "%SCRIPT%" 1 ^| more') do set "%2=%%_"
ENDLOCAL
exit /b


:SHOW_VARIABLE
SETLOCAL ENABLEDELAYEDEXPANSION
set VAR=%1
if /i "%DEBUG%"=="true" echo>&2 VAR=!VAR!
set RESULT=!VAR!
call :SHOW_VARIABLE_VALUE !%VAR%!
set RESULT=!RESULT!="!DATA!"
echo>&2 !RESULT!
ENDLOCAL
goto :EOF

:SHOW_VARIABLE_VALUE
set VAL=%1
if /i "%DEBUG%"=="true" echo>&2 %1
set DATA=%VAL%
if /i "%DEBUG%"=="true" echo>&2 VALUE=%VAL%
goto :EOF


:SHOW_LAST_ARGUMENT
REM https://stackoverflow.com/questions/1291941/batch-files-number-of-command-line-arguments
set /A ARGS_COUNT=0
for %%_ in (%*) DO SET /A ARGS_COUNT+=1
if /i "%DEBUG%"=="true" echo>&2 The number of arguments is %ARGS_COUNT%
REM the following does not work
SETLOCAL ENABLEDELAYEDEXPANSION
for /F "tokens=*" %%_ in ('echo %%!ARGS_COUNT!') DO set P=%%_
if /i "%DEBUG%"=="true" echo P=%P%
call :SHOW_VARIABLE_VALUE !P!
set CLEAN=%VALUE%
REM the value disappears after ENDLOCAL
ENDLOCAL
goto :EOF
