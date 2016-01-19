# Just Another Commons Library, Eh? (JACLE)

## Overview

This is a common base library with minimum dependencies that augments the basic JDK. Think Google's Guava. The modules include:

__commons__

*   The main base library

__commons-test__

*   A library specifically intended for use in unit testing (to be included at "test" scope)

__commons-unit-tests__

*   The unit tests for commons (happens to use commons-test, so it was broken out). This is not intended for consumption by other products.

## Developer Notes

See [Developer Notes](developer-notes.md) if you intend to build/contribute.

## License

    Copyright 2014 Moebius Solutions, Inc.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Revision History

### jacle-1.10

* Updated:
    * Reduced Guava dedependency to 13.0

### jacle-1.9

* Added:
    * FilesExt.copyDir()
    * FilesExt.moveDirByCopy()
    * DirUtils (from github.com/bbejeck/Java-7/)

### jacle-1.8

* Added:
    * JUnitFiles(File) constructor
    * CommonDateFormats.parse...() methods
    * StringBuilderExt

### jacle-1.7

* Added:
    * FilesExt.append()

### jacle-1.6

* Added:
    * JavaUtil.getSimpleFullName()
    * PropertiesUtils.fromResource()
    * FilesExt.getRelativeFile()
    * FilesExt.getRelativePath()
* Fixed:
    * Updated git flow notes for latest git flow version

### jacle-1.5

* Added:
    * TempJavaProperty
    * StringsExt.compare()
    * StringsExt.compareIgnoreCase()
    * FilesExt.getCanonicalPath()
    * FilesExt.getCanonicalFile()
    * FilesExt.write()
    * FilesExt.deleteEmptyDirAndParents()
    * FilesExt.toString()
    * Handler
    * KeyValue
    * StringExt.substring()
    * StringExt.prefixLines()
    * PrefixLogFormatte
    * FilesExt.walkFileTree()
    * FilesExt.move()
    * Provider
    * UrlParser
    
* Fixed:
    * These now do better symlink detection (and avoid bug due to inconsistent getAbsolutePath())
        * FilesExt.deleteDirectoryContents()
        * FilesExt.deleteRecursively()

### jacle-1.4

* Added:
    * FilesExt.createParentDirs()
    * FilesExt.newInputStream()
    * FilesExt.newOutputStream()
    * StringsExt.toStream()
    * StringsExt.fromStream()
    * PropertiesUtils

### jacle-1.3

* Added:
    * RemoteThrowableSerializer
    * Gson dependency 

### jacle-1.2

* Added:
    * AssertExt and AssertList
    * StringsExt
    * junit dependency to jacle-commons-test

### jacle-1.1

* Added:
    * FilesExt.mkdirs()
    * JavaUtil.getPackageName()
    * SystemOption.getFile()

### jacle-1.0

*   Initial open source release


