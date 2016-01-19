# Revision History

## jacle-1.11

* Updated:
    * Changed maven groupId to "com.github.MoebiusSolutions.jacle"
* Internal Changes:
	* Added build settings for travis ci
	* Split up documentation

## jacle-1.10

* Updated:
    * Reduced Guava dedependency to 13.0

## jacle-1.9

* Added:
    * FilesExt.copyDir()
    * FilesExt.moveDirByCopy()
    * DirUtils (from github.com/bbejeck/Java-7/)

## jacle-1.8

* Added:
    * JUnitFiles(File) constructor
    * CommonDateFormats.parse...() methods
    * StringBuilderExt

## jacle-1.7

* Added:
    * FilesExt.append()

## jacle-1.6

* Added:
    * JavaUtil.getSimpleFullName()
    * PropertiesUtils.fromResource()
    * FilesExt.getRelativeFile()
    * FilesExt.getRelativePath()
* Fixed:
    * Updated git flow notes for latest git flow version

## jacle-1.5

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

## jacle-1.4

* Added:
    * FilesExt.createParentDirs()
    * FilesExt.newInputStream()
    * FilesExt.newOutputStream()
    * StringsExt.toStream()
    * StringsExt.fromStream()
    * PropertiesUtils

## jacle-1.3

* Added:
    * RemoteThrowableSerializer
    * Gson dependency 

## jacle-1.2

* Added:
    * AssertExt and AssertList
    * StringsExt
    * junit dependency to jacle-commons-test

## jacle-1.1

* Added:
    * FilesExt.mkdirs()
    * JavaUtil.getPackageName()
    * SystemOption.getFile()

## jacle-1.0

*   Initial open source release
