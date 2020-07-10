#! /bin/bash

versionfile="VersionData.kt"
projectpath="$HOME/IdeaProjects/FatashiLinux"
filepath="$projectpath/src/org/umoja4life/fatashiBackend/$versionfile"

version_regex='(v[0-9]+\.[0-9]+\.[0-9]+)\s*(<.+>)?'

version=`cat $filepath | grep _GIT_VERSION_TAG | sed -s 's/^.*=//;s/"//g'`

if [ "$version" != "" ]; 
then
    if [[ $version =~ $version_regex ]]; 
    then
      git tag -a "${BASH_REMATCH[1]}" -m "${BASH_REMATCH[2]}" 
      echo "Created a new tag, $version" >&2
    else
      echo "version regex didn't match" >&2
    fi
else
  echo "no tagging action taken" >&2
fi

