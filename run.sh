#!/bin/bash
# set -x

APP_NAME='swet'
APP_VERSION='0.0.9-SNAPSHOT'
PACKAGE='com.github.sergueik.swet'
DEFAULT_MAIN_CLASS='SimpleToolBarEx'

which xmllint > /dev/null

if [  $? -eq  0 ] ; then
  APP_VERSION=$(xmllint -xpath "/*[local-name() = 'project' ]/*[local-name() = 'version' ]/text()" pom.xml)
  PACKAGE=$(xmllint -xpath "/*[local-name() = 'project' ]/*[local-name() = 'groupId' ]/text()" pom.xml)
  APP_NAME=$(xmllint -xpath "/*[local-name() = 'project' ]/*[local-name() = 'artifactId' ]/text()" pom.xml)
  DEFAULT_MAIN_CLASS=$(xmllint -xpath "/*[local-name() = 'project' ]/*[local-name() = 'properties' ]/*[local-name() = 'mainClass']/text()" pom.xml)
fi

MAIN_CLASS=${1:-$DEFAULT_MAIN_CLASS}

DOWNLOAD_EXTERNAL_JAR=false
ALIAS='opal'
JARFILE_VERSION='1.0.4'
JARFILE="$ALIAS-$JARFILE_VERSION.jar"
URL="https://github.com/lcaron/${ALIAS}/blob/releases/V$JARFILE_VERSION/${ALIAS}-$JARFILE_VERSION.jar?raw=true"

if [[ $DOWNLOAD_EXTERNAL_JAR ]]
then
  if [[ ! -f "src/main/resources/$JARFILE" ]]
  then
    pushd 'src/main/resources/'
    wget -O $JARFILE -nv $URL
    popd
    # https://ftp.mozilla.org/pub/firefox/releases/40.0.3/mac/en-US/
  fi
fi
if $(uname -s | grep -qi 'Darwin')
then

  # https://www.java.com/en/download/help/version_manual.xml
  JAVA_VERSION=$('/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/java' -version 2>& 1| sed -n 's|^.*"\(.*\)\".*$|\1|p')
  if [ -z $JAVA_VERSION} ]; then
    JAVA_VERSION='1.8.0_121'
  fi
  export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk$JAVA_VERSION.jdk/Contents/Home

  # NOTE: No uniform way to query OSX for the installed maven version
  # If Maven is installed to 'Applications' need to adjust the command line to: "mdfind -onlyin '/Applications' -name mvn"
  M2=$(mdfind -onlyin "${HOME}/Downloads" -name mvn | sed -n 's|^\(.*\)/mvn$|\1|p'|head -1)

  if [ -z $M2 ] ; then
    MAVEN_VERSION='3.3.9'
    export M2_HOME="$HOME/Downloads/apache-maven-$MAVEN_VERSION"
    export M2="$M2_HOME/bin"
  else
    export M2
    export M2_HOME=$(echo $M2| sed 's|/bin||')
  fi
  export MAVEN_OPTS='-Xms256m -Xmx512m'
  export PATH=$M2_HOME/bin:$PATH
  # http://stackoverflow.com/questions/3976342/running-swt-based-cross-platform-jar-properly-on-a-mac
  LAUNCH_OPTS='-XstartOnFirstThread'
fi

mvn -Dmaven.test.skip=true package install
java $LAUNCH_OPTS -cp target/$APP_NAME-$APP_VERSION.jar:target/lib/* $PACKAGE.$MAIN_CLASS
