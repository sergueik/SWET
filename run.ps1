# currently accepts max 6 Java application parameters.
# Java application parameters cannot start with dash - [ParameterBindingException]
# TODO: refactor param with regards of MAIN_CLASS
[CmdletBinding()]
param(
  [Parameter(Mandatory = $false,Position = 0)]
  [string]$MAIN_CLASS = 'SimpleToolBarEx',
  [Parameter(Mandatory = $false,Position = 1)]
  [string]$JAVA_APP_PARAM1,
  [Parameter(Mandatory = $false,Position = 2)]
  [string]$JAVA_APP_PARAM2,
  [Parameter(Mandatory = $false,Position = 3)]
  [string]$JAVA_APP_PARAM3,
  [Parameter(Mandatory = $false,Position = 4)]
  [string]$JAVA_APP_PARAM4,
  [Parameter(Mandatory = $false,Position = 5)]
  [string]$JAVA_APP_PARAM5,
  [Parameter(Mandatory = $false,Position = 6)]
  [string]$JAVA_APP_PARAM6
)

try {
  $o = $PSBoundParameters.Values |
  where-object { $_ -ne $MAIN_CLASS } |
  foreach-object { write-output $_ }
  $JAVA_ARGS = $o -join ' '
} catch [parameterbindingexception]{}
if ($env:TOOLS_DIR -ne $null) {
  $TOOLS_DIR = $env:TOOLS_DIR
} else { $TOOLS_DIR = 'c:\java' }
if ($env:MAVEN_VERSION -ne $null) {
  $MAVEN_VERSION = $env:MAVEN_VERSION
} else {
  $MAVEN_VERSION = '3.3.9'
}
if ($env:JAVA_VERSION -ne $null) {
  $JAVA_VERSION = $env:JAVA_VERSION
} else { $JAVA_VERSION = '1.8.0_101'
}
if ($env:JAVA_HOME -eq $null) {
  $env:JAVA_HOME = "${TOOLS_DIR}\jdk${JAVA_VERSION}"
}
if ($env:M2_HOME -eq $null) {
  $env:M2_HOME = "${TOOLS_DIR}\apache-maven-${MAVEN_VERSION}"
}
if ($env:M2 -eq $null) {
  $env:M2 = "${env:M2_HOME}\bin"
}


$env:PATH = "${env:JAVA_HOME}\bin;${env:M2};${env:PATH}"
$env:JAVA_OPTS = $env:MAVEN_OPTS = @( '-Xms256m', '-Xmx512m')

# NOTE: Powershell / XML is somewhat time consuming. Uncomment as needed
# $APP_NAME = 'swet'
# $APP_VERSION = '0.0.9-SNAPSHOT'
# $PACKAGE = 'com.github.sergueik.swet'

if (($PACKAGE -eq $null ) -or ($APP_VERSION -eq $null) -or ($APP_NAME -eq $null ) ){
  $data = get-content -path 'pom.xml'
  $project = [xml]$data
}


if ($PACKAGE-eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    $PACKAGE = (
      # with apache pom schema
      # select-xml needs namespace argument hash
      # accepts arbitraty prefix to be defined in namespace argument hash and used in xpath.
      # empty key is not supported: '/:project/:version' has an invalid token.
      select-xml -xml $project -XPath '/a:project/a:groupId' `
        -Namespace @{'a'='http://maven.apache.org/POM/4.0.0';  } ).node.'#text'
  } else {
    $PACKAGE = $project.'project'.'groupId'
  }
}
if ($APP_VERSION -eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    # plain element name does not work, local-name() does
    $APP_VERSION = (
    (select-xml -xml $project -XPath '/*[local-name()="project"]/*[local-name()="version"]' ).node.'#text'
  } else {
    $APP_VERSION = $project.'project'.'version'
  }
}
if ($APP_NAME -eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    $APP_NAME = (select-xml -xml $project -XPath '/dom:project/dom:artifactId' `
      -Namespace @{'dom'='http://maven.apache.org/POM/4.0.0';  } ).node.'#text'
  } else {
    $APP_NAME = $project.'project'.'artifactId'
  }
}


# external dependencies
$DOWNLOAD_EXTERNAL_JAR = $false
$DEPENDENCIES = @{ 'opal' = '1.0.4'; }
if ($DOWNLOAD_EXTERNAL_JAR -eq $true) {
  $DEPENDENCIES.Keys | foreach-object {
    $ALIAS = $_;
    $JARFILE_VERSION = $DEPENDENCIES[$_];
    $JARFILE = "${ALIAS}-${JARFILE_VERSION}.jar"
    $JARFILE_LOCALPATH = (resolve-path '.\src\main\resources').path + '\' + $JARFILE
    if (-not (test-path -Path $JARFILE_LOCALPATH)) {
      $URI = "https://github.com/lcaron/opal/blob/releases/V${JARFILE_VERSION}/opal-${JARFILE_VERSION}.jar?raw=true"
      $request = invoke-webrequest -Uri $URI -MaximumRedirection 0 -ErrorAction ignore
      if ($request.StatusDescription -eq 'found') {
        $uri = $request.Headers.Location
        write-output ('downloading from {0}' -f $uri)
      }
      write-output "Downloading ${JARFILE_LOCALPATH}"
      invoke-webrequest -Uri $URI -OutFile $JARFILE_LOCALPATH
    }
  }
}
# compile
& 'mvn.cmd' '-Dmaven.test.skip=true' 'package' 'install'
# run
write-output (( @"
Run:
& 'java.exe' `
   '-cp' "target\${APP_NAME}-${APP_VERSION}.jar;target\lib\*" `
"${PACKAGE}.${MAIN_CLASS}" "${JAVA_ARGS}"
"@ -replace '`', '' ) -replace '\r?\n', '')
& 'java.exe' `
   '-cp' "target\${APP_NAME}-${APP_VERSION}.jar;target\lib\*" `
   "${PACKAGE}.${MAIN_CLASS}" "${JAVA_ARGS}"
