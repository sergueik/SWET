# currently accepts max 6 Java application parameters. 
# Java application parameters cannot begin wth dash - [ParameterBindingException] 
[CmdletBinding()]
Param (
[Parameter(Mandatory=$false,Position = 0)]
[string]$MAIN_APP_CLASS = 'SimpleToolBarEx',
[Parameter(Mandatory=$false,Position = 1)]
[string] $JAVA_APP_PARAM1,
[Parameter(Mandatory=$false,Position = 2)]
[string] $JAVA_APP_PARAM2,  
[Parameter(Mandatory=$false,Position = 3)]
[string] $JAVA_APP_PARAM3,
[Parameter(Mandatory=$false,Position = 4)]
[string] $JAVA_APP_PARAM4,
[Parameter(Mandatory=$false,Position = 5)]
[string] $JAVA_APP_PARAM5,
[Parameter(Mandatory=$false,Position = 6)]
[string] $JAVA_APP_PARAM6 ) 

try {  
  $o = $PSBoundParameters.Values | 
    where-object {$_ -ne $MAIN_APP_CLASS } | 
    foreach-object { write-output $_   }   
    $JAVA_ARGS = $o -join ' ' 
} catch [ParameterBindingException] { }
if ($env:TOOLS_DIR -ne $null) {
  $TOOLS_DIR = $env:TOOLS_DIR 
} else {  $TOOLS_DIR = 'c:\java' }
if ($env:MAVEN_VERSION -ne $null) {
  $MAVEN_VERSION = $env:MAVEN_VERSION
} else {
  $MAVEN_VERSION = '3.3.9'
}
if ($env:JAVA_VERSION -ne $null){
  $JAVA_VERSION = $env:JAVA_VERSION
} else {  $JAVA_VERSION = '1.8.0_101'
}
if ($env:JAVA_HOME -eq $null){
  $env:JAVA_HOME = "${TOOLS_DIR}\jdk${JAVA_VERSION}"
}
if ($env:M2_HOME -eq $null) {
  $env:M2_HOME = "${TOOLS_DIR}\apache-maven-${MAVEN_VERSION}"
}
if ($env:M2 -eq $null) {
  $env:M2 = "${env:M2_HOME}\bin"
}
$env:PATH = "${env:JAVA_HOME}\bin;${env:M2};${env:PATH}"
$env:JAVA_OPTS = $env:MAVEN_OPTS = @('-Xms256m','-Xmx512m')
$PACKAGE_NAME = 'swet'
$PACKAGE_VERSION = '0.0.7-SNAPSHOT'
$MAIN_APP_PACKAGE = 'org.swet'
# external dependencies
$DOWNLOAD_EXTERNAL_JAR = $false
$DEPENDENCIES = @{ 'opal' = '1.0.4'; }
if ($DOWNLOAD_EXTERNAL_JAR -eq $true ) { 
  $DEPENDENCIES.Keys | ForEach-Object {
    $ALIAS = $_;
    $JARFILE_VERSION = $DEPENDENCIES[$_];
    $JARFILE = "${ALIAS}-${JARFILE_VERSION}.jar"
    $JARFILE_LOCALPATH = (Resolve-Path '.\src\main\resources').path + '\' + $JARFILE
    if (-not (Test-Path -Path $JARFILE_LOCALPATH)) {
      $URI = "https://github.com/lcaron/opal/blob/releases/V${JARFILE_VERSION}/opal-${JARFILE_VERSION}.jar?raw=true"
      $request = Invoke-WebRequest -uri $URI -MaximumRedirection 0 -ErrorAction ignore
      if ($request.StatusDescription -eq 'found') {
        $uri = $request.Headers.Location
        Write-Output ('downloading from {0}' -f $uri)
      }
      write-output "Downloading ${JARFILE_LOCALPATH}"
      Invoke-WebRequest -uri $URI -OutFile $JARFILE_LOCALPATH
    }
  }
}
# Compile
& 'mvn.cmd' '-Dmaven.test.skip=true' 'package' 'install'
# Run
& 'java.exe' `
'-cp' "target\${PACKAGE_NAME}-${PACKAGE_VERSION}.jar;target\lib\*" `
"${MAIN_APP_PACKAGE}.${MAIN_APP_CLASS}" "${JAVA_ARGS}"
