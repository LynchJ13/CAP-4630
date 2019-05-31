#!/bin/sh
# Get name of current dev-branch
branch_name=$(git symbolic-ref -q HEAD)
branch_name=${branch_name##refs/heads/}
branch_name=${branch_name:-HEAD}
# Commit current dev branch for merge
git add *
git commit -m "Commit for $branch_name"
# Switch to master branch
git push
echo "
SUCCESS: Changes have been successfully pushed to the your dev branch!
"
read