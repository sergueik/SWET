#!/bin/bash

# based on:
# https://www.cyberforum.ru/shell/thread2822786.html

get_text() {

TAG=${1:-version}
PROJECT_FILE=${2:-pom.xml}

if [[ "$DEBUG" = 'true' ]]; then
  1>&2 printf "started get_text() with TAG=%s PROJECT_FILE=%s\n" $TAG $PROJECT_FILE
fi  
RESULT=''
PRINTED='false'
IN_PROJECT='false'
IN_DEPENDENCIES='false'
while read LINE;
do
  if [[ "$LINE" =~ '<project '  || "$LINE" =~ '<project>' ]]; then
    IN_PROJECT='true'
    RESULT=''
    PRINTED='false'
  fi
  if [ $(expr match "$LINE" '<dependencies') != '0' ]; then
    IN_DEPENDENCIES='true'
  fi
  if [[ $LINE =~ '</project>' ]]; then
    IN_PROJECT='false'
  fi
  # TODO: exclude scanning <dependencies> etc. 
  if [ $(expr match "$LINE" '</dependencies') != '0' ]; then
    IN_DEPENDENCIES='false'
  fi

  if [ "$IN_PROJECT" == 'true' ] ; then

    if [ $(expr match "$LINE" "<${TAG}>") != '0' ]; then

      RESULT=$(echo $LINE | sed "s/<\\/\\{0,1\\}${TAG}>//g")
      if [[ "$DEBUG" = 'true' ]]; then
        1>&2 printf "Found result:%s\n" $RESULT
      fi  
      # return result immediately
      echo $RESULT
      return
    fi
  fi
done <$PROJECT_FILE
}

PROJECT_FILE=${1:-pom.xml}
if [[ "$DEBUG" = 'true' ]]; then
  1>&2 echo 'Started.'
fi
TAG='version'
APP_VERSION=$(get_text $TAG $PROJECT_FILE)
echo "APP_VERSION=${APP_VERSION}"

TAG='groupId'
PACKAGE=$(get_text $TAG $PROJECT_FILE)
echo "PACKAGE=${PACKAGE}"

TAG='artifactId'
APP_NAME=$(get_text $TAG $PROJECT_FILE)
echo "APP_NAME=${APP_NAME}"

TAG='mainClass'
DEFAULT_MAIN_CLASS=$(get_text $TAG $PROJECT_FILE)
echo "DEFAULT_MAIN_CLASS=${DEFAULT_MAIN_CLASS}"

if [[ "$DEBUG" = 'true' ]]; then
  1>&2 echo 'Done.'
fi
