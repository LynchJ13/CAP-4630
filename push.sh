#!/bin/sh
# Get name of current dev-branch
branch_name=$(git symbolic-ref -q HEAD)
branch_name=${branch_name##refs/heads/}
branch_name=${branch_name:-HEAD}
# Commit current dev branch for merge
git add *
git commit -m "Pre-merge commit for production"
# Switch to master branch
git checkout master
# initiate merge
git merge $branch_name
# Check for conflicts
conflicts=$(git ls-files -u | wc -l)
if [ "$conflicts" -gt 0 ] ; then
	git merge --abort
	echo One or merge conflicts detected, resetting to before attempted merge.
	git checkout $branch_name
	read
	exit 1
fi
# Return to original branch
git commit -m "Successfull merge"
git push
echo "
SUCCESS: Changes have been successfully pushed to the master branch!
"
git checkout $branch_name
read