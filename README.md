# Just Another Commons Library, Eh? (JACLE)

## Overview

This is a common base library with minimum dependencies that augments the basic JDK. Think Google's Guava. The modules include:

__commons__ - The main library

    <dependency>
        <groupId>com.github.MoebiusSolutions.jacle</groupId>
        <artifactId>jacle-commons</artifactId>
        <packaging>pom</packaging>
        <version>${latest-version}</version>
    </dependency>

__commons-test__ - A library specifically intended for use in unit testing (to be included at "test" scope)

    <dependency>
        <groupId>com.github.MoebiusSolutions.jacle</groupId>
        <artifactId>jacle-commons-test</artifactId>
        <packaging>pom</packaging>
        <version>${latest-version}</version>
        <scope>test</scope>
    </dependency>

__commons-unit-tests__ - The unit tests for commons (happens to use commons-test, so it was broken out). This is not intended for consumption by other products.

## Downloading 

This library is now availabe in the following maven repos: 

* [Maven central](http://mvnrepository.com/) - The default maven repository
** [Jacle's page](http://mvnrepository.com/artifact/com.github.MoebiusSolutions.jacle)
** [versions available](http://mvnrepository.com/artifact/com.github.MoebiusSolutions.jacle/jacle-commons)
* [JCenter](https://bintray.com/bintray/jcenter) - An alternative maven repository, hosted on [Bintray](https://bintray.com) 
** The repo's [maven url](http://jcenter.bintray.com/)
** [Jacle's page](https://bintray.com/moebiussolutions/JACLE/jacle/view) shows version's available

## More Info

* [Developer Notes](developer-notes.md) - If you intend to build/contribute.
* [Software License](license.md)
* [Release Notes](release-notes.md)


