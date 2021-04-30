#!/bin/bash

# based on:
# https://www.cyberforum.ru/shell/thread2822786.html
PROJECT_FILE=${1:-pom.xml}
VERSION=''
ARTIFACTID=''
GROUPID=''
# only kept for illustration: there is no multiple GAV declarations in pom
PRINTED='false'
IN_PROJECT='false'
IN_DEPENDENCIES='false'
while read LINE;
do
  # see 
  # https://tldp.org/LDP/abs/html/abs-guide.html#REGEXMATCHREF
  if [[ "$LINE" =~ '<project '  || "$LINE" =~ '<project>' ]]; then
    IN_PROJECT='true'
    GROUPID=''
    ARTIFACTID=''
    VERSION=''
    PRINTED='false'
  fi
  # the below does not require bash
  # is same but more verbose, not easy to combine with other
  # https://tldp.org/LDP/abs/html/string-manipulation.html
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
    if [ $(expr match "$LINE" '<version>') != '0' ]; then
      VERSION=$(echo $LINE | sed 's/<\/\{0,1\}version>//g')
    fi
    if [[ "$LINE" =~ '<groupId>' ]]; then
      GROUPID=$(echo $LINE | sed 's/<\/*groupId>//g')
    fi
    if [[ "$LINE" =~ '<artifactId>' ]]; then
      ARTIFACTID=$(echo $LINE | sed 's/<\/*artifactId>//g')
    fi
    # the following will not find anything in pom.xml
    # kept for the reference
    if [[ $LINE =~ '<date>' ]]; then
      value=`echo $LINE | sed 's/<\/*date>//g'`
      date=`echo $value | sed 's/^[[:alpha:]]\{3\}, \([[:digit:]]\{2\} [[:alpha:]]\{3\} [[:digit:]]\{4\} [[:digit:]]\{2\}:[[:digit:]]\{2\}:[[:digit:]]\{2\}\) +[[:digit:]]\{4\}/\1/g'`
    fi

    if [[ "$GROUPID" != '' && "$ARTIFACTID" != "" && "$VERSION" != '' && $PRINTED == 'false' && "$IN_DEPENDENCIES" == 'false' ]]; then
      printf "GROUPID=%s ARTIFACTID=%s VERSION=%s\n" $GROUPID $ARTIFACTID $VERSION
      PRINTED='true'
    fi
  fi
done <$PROJECT_FILE
# alternatively:
# done <  <(cat $PROJECT_FILE)

# GROUPID=com.github.sergueik.swet ARTIFACTID=swet VERSION=0.17.0-SNAPSHOT
# GROUPID=example ARTIFACTID=commandline-parser VERSION=${commandline-parser.version}
