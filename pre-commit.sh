#!/bin/bash
#
# environmental args expected:
# BUMP_MAJOR_VERSION, BUMP_MINOR_VERSION, VERSION_COMMENT
# if either of the bumps are true, then increases that version, resets the other (lesser) versions
# afterwards, removes those flags
# VERSION_COMMENT can be any comment which will be added to the version data file
# for example: a projectname -- robin, bluebird, apex, etc; VERSION_COMMENT will persist
#
versionfile="VersionData.kt"
projectpath="$HOME/IdeaProjects/FatashiBackend"
filepath="$projectpath/src/org/umoja4life/fatashibackend/$versionfile"

version_regex='v([0-9]+)\.([0-9]+)\.([0-9]+)'
git_string=$(git describe --tags)

if [[ $git_string =~ $version_regex ]]; then
    major_version="${BASH_REMATCH[1]}"
    minor_version="${BASH_REMATCH[2]}"
    patch_version="${BASH_REMATCH[3]}"
else
    echo "Error: git describe did not output a valid version string. Unable to update $versionfile" >&2
    exit 1
fi

# logic

if [ -n "$BUMP_MAJOR_VERSION" ]
then
   let major_version+=1
   minor_version=1
   patch_version=0
else if [ -n "$BUMP_MINOR_VERSION" ]
  then
    let minor_version+=1
    patch_version=0
  else
    let patch_version+=1
  fi
fi

if [ -z "$VERSION_COMMENT" ]
then
  version_comment=""
else
  version_comment="<$VERSION_COMMENT>"
fi

#

version_string="v${major_version}.${minor_version}.${patch_version} $version_comment"

echo "updating version number to: $version_string in file $versionfile" >&2
unset BUMP_MAJOR_VERSION BUMP_MINOR_VERSION

# cat  <<EOM
cat > $filepath <<EOM
package org.umoja4life.fatashibackend
// *******************************************************************************
// NOTE: this file is automatically updated by .git/hooks/pre-commit script
// *******************************************************************************
// Uses environmental variables: BUMP_MAJOR_VERSION, BUMP_MINOR_VERSION, VERSION_COMMENT
//     to control logic in pre-commit hook. Use bump.sh to auto bump appropriate level.
// $ . ./bump.sh minor "version comment"
// The const values below are ONLY used to initialize version.kt enum Version.
// *******************************************************************************
// WARNING: pre-commit and post-commit git hooks reference exact path to this file
// ********> $filepath <********
// **** you must change those scripts if you change the actual file location ****
// *******************************************************************************

const val _MAJOR_VERSION=$major_version
const val _MINOR_VERSION=$minor_version
const val _PATCH_VERSION=$patch_version
const val _VERSION_COMMENT="$version_comment"
const val _GIT_VERSION_TAG="$version_string"

EOM

# echo "filepath is: $filepath"

git add $filepath
