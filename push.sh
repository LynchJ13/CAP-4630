#!/bin/sh
echo Enter a description of the changes being pushed:
read desc
git add .
git commit -m "$desc"
git push
echo Changes successfully pushed. Press any key to close...
read