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
  [string]$JAVA_APP_PARAM6,
  [Parameter(Mandatory = $false)]
  [switch]$clean
)

try {
  $o = $PSBoundParameters.Values |
  where-object { $_ -ne $MAIN_CLASS } |
  foreach-object {
    write-output $_
  }
  $JAVA_ARGS = $o -join ' ';
} catch [parameterbindingexception]{}

if ($env:TOOLS_DIR -ne $null) {
  $TOOLS_DIR = $env:TOOLS_DIR;
} else {
  $TOOLS_DIR = 'c:\java';
}
if ($env:MAVEN_VERSION -ne $null) {
  $MAVEN_VERSION = $env:MAVEN_VERSION;
} else {
  $MAVEN_VERSION = '3.6.1';
}
if ($env:JAVA_VERSION -ne $null) {
  $JAVA_VERSION = $env:JAVA_VERSION
} else {
  $JAVA_VERSION = '1.8.0_101'
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

$build_clean = [bool]$PSBoundParameters['clean'].IsPresent
$skip_build = [bool](([System.Environment]::getEnvironmentVariable('SKIP_BUILD', [System.EnvironmentVariableTarget]::User)) -ne $null)

# Clear the environment entry that is created by git bash when starting powershell and ruins Maven 3.5.0 ANSI colors.
[Environment]::SetEnvironmentVariable('TERM', $null,'Process')
# write-output $env:TERM

$env:PATH = "${env:JAVA_HOME}\bin;${env:M2};${env:PATH}"
$env:JAVA_OPTS = $env:MAVEN_OPTS = @( '-Xms256m', '-Xmx512m')

# early versions os SWET there was an external jar dependency
# on Opal Project 'SWT new widgets' library
$DEPENDENCIES = @{
  'opal' = '1.0.9';
}
# download external dependency jars
$DOWNLOAD_EXTERNAL_JAR = $false
# $DOWNLOAD_EXTERNAL_JAR = $true

# NOTE: powershell / XML is somewhat time consuming. Uncomment as needed

# $APP_NAME = 'swet'
# $APP_VERSION = '0.0.10-SNAPSHOT'
# $PACKAGE = 'com.github.sergueik.swet'
# $MAIN_CLASS = 'SimpleToolBarEx'
# $SCM_CONNECTION = $null

write-debug 'Reading the parameters from "pom.xml"'
if (($PACKAGE -eq $null ) -or ($APP_VERSION -eq $null) -or ($APP_NAME -eq $null ) ){
  $data = get-content -path 'pom.xml'
  $project = [xml]$data
}


if ($PACKAGE -eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    $PACKAGE = (
      # apache pom namespace schema has to be honored by
      # select-xml via namespace argument hash which it will use in resolving xpath.
      # except with empty key, which will not work: '/:project/:version' has an invalid token.
      select-xml -xml $project -XPath '/a:project/a:groupId' `
        -Namespace @{'a'='http://maven.apache.org/POM/4.0.0';  } ).node.'#text'
  } else {
    # use VB-style MS XML traversatl DSL with namespaces ignored, will work with pom.xml because pom.xml uses empty prefix of default namespace
    $PACKAGE = $project.'project'.'groupId'
  }
}

write-debug ('PACKAGE={0}' -f $PACKAGE)


if ($DEFAULT_MAIN_CLASS -eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    $DEFAULT_MAIN_CLASS = (
      # alternative way of dealing with schema XML is
      # use local-name() in xpath, like with xmlint
      select-xml -xml $project -XPath '/*[local-name()="project"]/*[local-name()="properties"]/*[local-name()="mainClass"]' ).node.'#text'
  } else {
    # use VB-style MS XML traversatl DSL with namespaces ignored, will work with pom.xml because pom.xml uses empty prefix of default namespace
    $DEFAULT_MAIN_CLASS = $project.'project'.'properties'.'mainClass'
  }
}
write-debug ('DEFAULT_MAIN_CLASS={0}' -f $DEFAULT_MAIN_CLASS)

if ($APP_VERSION -eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    $APP_VERSION = ( select-xml -xml $project -XPath '/*[local-name()="project"]/*[local-name()="version"]' ).node.'#text'
  } else {
    # use VB-style MS XML traversatl DSL
    $APP_VERSION = $project.'project'.'version'
  }
}
write-debug ('APP_VERSION={0}' -f $APP_VERSION)

# based on:
# https://www.cyberforum.ru/powershell/thread3017210.html#post16425705
# the code below would also work with SOAP envelope XML 
# where every tag is prefixed and there is no default blank namespace
$XmlDocument = [xml]$data
# NOTE: cannot strongly type:
# [System.Xml.DocumentXPathNavigator] $navigator = ...
# Unable to find type [System.Xml.DocumentXPathNavigator]
$navigator = $XmlDocument.CreateNavigator()

# MS.Internal.Xml.XPath.CompiledXpathExpr
$query = $navigator.Compile('/dom:project/dom:artifactId')

$manager = [Xml.XmlNamespaceManager]::new($navigator.NameTable)
$manager.AddNamespace('xsi', 'http://www.w3.org/2001/XMLSchema-instance')
$manager.AddNamespace('schemaLocation', 'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd')
$manager.AddNamespace('dom', 'http://maven.apache.org/POM/4.0.0')
$query.SetContext($manager)
# NOTE: cannot strongly type:
# [System.Xml.DocumentXPathNavigator]$nodes = ...
# Unable to find type [System.Xml.DocumentXPathNavigator]
$nodes = $navigator.Select($query)
$APP_NAME = $nodes.InnerXml

if ($APP_NAME -eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    $APP_NAME = (select-xml -xml $project -XPath '/dom:project/dom:artifactId' `
      -Namespace @{'dom'='http://maven.apache.org/POM/4.0.0';  } ).node.'#text'
  } else {
    # use VB-style MS XML traversatl DSL
    $APP_NAME = $project.'project'.'artifactId'
  }
}
write-debug ('APP_NAME={0}' -f $APP_NAME)

if ($SCM_CONNECTION -eq $null ){
  if ($PSVersionTable.PSVersion.Major  -gt 3 ) {
    $SCM_CONNECTION = (select-xml -xml $project -XPath '/dom:project/dom:scm/dom:connection' `
      -Namespace @{'dom'='http://maven.apache.org/POM/4.0.0';  } ).node.'#text'
  } else {
    # use VB-style MS XML traversatl DSL
    $SCM_CONNECTION = $project.'project'.'scm'.'connection'
  }
  $SCM_CONNECTION = $SCM_CONNECTION -replace '^scm:git:', ''
}
write-debug ('SCM_CONNECTION={0}' -f $SCM_CONNECTION)

if ($DOWNLOAD_EXTERNAL_JAR -eq $true) {
  $DEPENDENCIES.Keys | foreach-object {
    $ALIAS = $_;
    $JARFILE_VERSION = $DEPENDENCIES[$_];
    $JARFILE = "${ALIAS}-${JARFILE_VERSION}.jar"
    $JARFILE_LOCALPATH = (resolve-path '.\src\main\resources').path + '\' + $JARFILE
    if (-not (test-path -Path $JARFILE_LOCALPATH)) {
      [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
      $URI = "https://github.com/lcaron/opal/blob/releases/V${JARFILE_VERSION}/opal-${JARFILE_VERSION}.jar?raw=true"
      write-debug ("Running invoke-webrequest -Uri $URI -MaximumRedirection 0 -ErrorAction ignore")
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
if ($build_clean ) {
  write-debug ("Run: & 'mvn.cmd' '-Dmaven.test.skip=true' 'clean' 'package' 'install'")
  & 'mvn.cmd' '-Dmaven.test.skip=true' 'clean' 'package' 'install'
} else {
  write-debug ("Run: & 'mvn.cmd' '-Dmaven.test.skip=true' 'package' 'install'")
  & 'mvn.cmd' '-Dmaven.test.skip=true' 'package' 'install'
}
# run
write-debug (( @"
Run:
& 'java.exe' `
   '-cp' "target\${APP_NAME}-${APP_VERSION}.jar;target\lib\*" `
"${PACKAGE}.${MAIN_CLASS}" "${JAVA_ARGS}"
"@ -replace '`', '' ) -replace '\r?\n', '')
& 'java.exe' `
   '-cp' "target\${APP_NAME}-${APP_VERSION}.jar;target\lib\*" `
   "${PACKAGE}.${MAIN_CLASS}" "${JAVA_ARGS}"

if ($build_clean ) {
  # if the run.ps1 was "sourced" the variables stay defined
  $APP_NAME = $null
  $APP_VERSION = $null
  $PACKAGE =  $null
  $MAIN_CLASS =  $null
}
