# Just Another Commons Library, Eh? (JACLE)

## Overview

This is a common base library with minimum dependencies that augments the basic JDK. Think Google's Guava. The modules include:

__commons__

*   The main base library

__commons-test__

*   A library specifically intended for use in unit testing (to be included at "test" scope)

__commons-unit-tests__

*   The unit tests for commons (happens to use commons-test, so it was broken out). This is not intended for consumption by other products.

## Branches and Releases

Important points:

*   The main work-in-progress branch is "develop", __not__ "master", this is the convention of git-flow.
*   This project uses __git-flow__, with default settings, except that release tags are prefixed with "jacle-".

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
    Version tag prefix? [v]

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

    $ git push -u origin release-1.0

Run a build of the branch on the server:

*   We have a [jacle branch](https://build.moesol.com/jenkins/job/jacle-commons%20branch/) build on Jenkins that takes a parameter of the branch name (e.g. "release-1.0").

Repeatedly commit, push, and build until the build is stable.

Finish the release, and push to the server (when prompted, enter release notes):

    $ git flow release finish -p 1.0

(now on the "develop" branch)

Update versions in pom files and pushs:

    $ mvn versions:set -DgenerateBackupPoms=false -DnewVersion=1.1-SNAPSHOT
    $ git add -A :/
    $ git commit -m 'Rolling version to 1.1-SNAPSHOT'
    $ git push

Finally, deploy the following files from the build to Artifactory:

    jacle-commons-1.0.jar
    jacle-commons-1.0.pom
    jacle-commons-test-1.0.jar
    jacle-commons-test-1.0.pom

## Revision History

### jacle-1.0

*   Initial open source release
