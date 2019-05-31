#!/bin/sh
ignore_file="./.git/info/exclude"
if grep -q git "$ignore_file" ; then
  echo Found
else
  echo not found
fi

read