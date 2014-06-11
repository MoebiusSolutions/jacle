# moesol-commons

## Overview

This is a common base library with minimum dependencies. Think Google's Guava. The modules include:

__moesol-commons__

*   The main base library

__moesol-commons-test__

*   A library specifically intended for use in unit testing (to be included at "test" scope)

__moesol-commons-testing__

*   The unit tests for moesol-commons (happens to use moesol-commons-test, so it was broken out)

## Branches and Releases

Important points:

*   The main work-in-progress branch is "develop", __not__ "master", this is the convention of git-flow.
*   This project uses __git-flow__, with default settings, except that release tags are prefixed with "moesol-commons-".

Here's a quick overview of the branching pattern:

*   [http://nvie.com/posts/a-successful-git-branching-model/](http://nvie.com/posts/a-successful-git-branching-model/)
*    Note, ignore the commands in this overview. The git-flow tool automates much of this.

## Release Process

### Setting Up git-flow

Before doing anything else, please install the [git-flow plugin](https://github.com/nvie/gitflow/blob/develop/README.mdown) to your git installation.


After installing git-flow, checkout the "develop" branch:

    $ git checkout -b develop origin/develop

The be sure to "git flow init" your local repo, which populates your ".git/config" repo file with git-flow options. The important point here is to set these values (mindful of the final value):

    $ git flow init -f

    Which branch should be used for integration of the "next release"?
       - develop
       - releases
    Branch name for "next release" development: [develop] develop
    
    How to name your supporting branch prefixes?
    Feature branches? [feature-]
    Release branches? [release-]
    Hotfix branches? [hotfix-]
    Support branches? [support/]
    Version tag prefix? [moesol-commons-]

### Performing a Release

When you have changes in the "develop" branch that you'd like to release, follow these steps...

Checkout the latest in-progress branch:

    $ git checkout develop

Begin the release process (on a new release branch), where "1.0" is your target version:

    $ git flow release start 1.0

(now on the "release-1.0" branch)

Update versions in pom files:

    $ mvn versions:set -DgenerateBackupPoms=false -DnewVersion=1.0
    $ git add -A :/
    $ git commit -m 'Rolling version to 1.0'

Update the release notes in the README.md file:

    $ vi README.md
    $ git add README.md
    $ git commit -m 'Updated release notes'

Push changes to the server:

    $ git push -u origin release-1.2

Run a build of the branch on the server:

*   We have a [moesol-commons release](https://build.moesol.com/jenkins/job/moesol-commons%20branch/) build on Jenkins that takes a parameter of the branch name (e.g. "release-1.0")

Repeatedly commit, push, and build until the build is stable.

Finish the release (ending the release branch and tagging a release). When prompted, enter release notes.

    $ git flow release finish -p 1.0

(now on the "develop" branch)

Update versions in pom files:

    $ mvn versions:set -DgenerateBackupPoms=false -DnewVersion=1.1-SNAPSHOT
    $ git add -A :/
    $ git commit -m 'Rolling version to 1.1-SNAPSHOT'

Push everything to the server (note: I think we can skip some of this with the "-p" option above, but it needs verification on the next release):

    # Push the master branch (now has latest release)
    git checkout master
    git push -u origin master
    
    # Push the develop branch (now has any merged changes from the release branch)
    git checkout develop
    git push -u origin develop
    
    # Push the release tag
    git push -u origin moesol-commons-1.0
    
    # Delete the release branch from the server
    git push origin :release-1.0

## Revision History

### moesol-commons-1.3

*   Added README.md with release process and revision history

### moesol-commons-1.2

*   First Release

### moesol-commons-1.1

*   Just fussing with git-flow

### moesol-commons-1.0

*   Just fussing with git-flow

