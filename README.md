# Just Another Commons Library, Eh? (JACLE)

## Overview

This is a common base library with minimum dependencies that augments the basic JDK. Think Google's Guava. The modules include:

__commons__ - The main base library

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

## More Info

* [Developer Notes](developer-notes.md) - If you intend to build/contribute.
* [Software License](license.md)
* [Release Notes](release-notes.md)


