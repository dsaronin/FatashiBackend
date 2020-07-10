#!/bin/bash

export my_name="bump"
export usage="$my_name major|minor|patch [<version_comment>|clear]"

function print_blue()
{
  echo  -e '\E[1;34m'"$1"'\E[1m\E[0m'
}

function print_warn()
{
  echo  -e '\E[1;32m'"$1"'\E[1m\E[0m'
}

function print_err()
{
  echo  -e '\E[1;31m'"$my_name: OOPS .. $1"'\E[1m\E[0m' >&2
}

# regex to match first command
cmd_regex='major|minor|patch'

# ensure that a command is present and its valid
if [[ $1 =~ $cmd_regex ]]; 
then
  unset BUMP_MAJOR_VERSION BUMP_MINOR_VERSION
  print_blue "clearing all flags"
else
  print_err "bump type required: $usage"
fi

# is a possible version comment present?
if [ -n "$2" ]
then
     # if it matches "clear" then unset the env variable
  if [ "$2" = "clear" ] 
  then
    unset VERSION_COMMENT
  else
    export VERSION_COMMENT="$2"
    print_warn "comment set to: \"$2\""
  fi
fi

# switch for command bump types
case $1 in
  major)
    export BUMP_MAJOR_VERSION=1
    print_warn "MAJOR VERSION BUMP"
    ;;
  minor)
    export BUMP_MINOR_VERSION=1
    print_warn "MINOR VERSION BUMP"
    ;;
  patch)
    print_warn "PATCH VERSION BUMP"
    ;;
esac


echo ">>> $BUMP_MAJOR_VERSION, $BUMP_MINOR_VERSION, $VERSION_COMMENT"
