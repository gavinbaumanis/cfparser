cfparser
========
[![Build Status](https://travis-ci.org/cfparser/cfparser.svg?branch=java-11)](https://travis-ci.org/cfparser/cfparser)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b25fc5beacea4d4f9c493971fcfb7e90)](https://www.codacy.com/app/ryaneberly/cfparser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cfparser/cfparser&amp;utm_campaign=Badge_Grade)

CFParser is on Maven Central. License: BSD (http://www.opensource.org/licenses/bsd-license.html).

This is the Java 11 line. Published artifacts use version `11.0.0` and require Java 11+.

Modules: `cfml.parsing` (grammar and AST), `cfml.dictionary` (tag and function metadata), `cfml.cli` (optional native CLI).

Integrator entry points: `CFMLParser`, `CFMLSource`, `DictionaryManager`, `SyntaxDictionary`.

## Build

Libraries (dictionary + parsing + CLI jar):

```
mvn clean install
```

Native CLI image (needs GraalVM):

```
mvn -pl cfml.cli -am package -Pnative
```

## Dependency

```xml
<dependency>
    <groupId>com.github.cfmleditor</groupId>
    <artifactId>cfml.parsing</artifactId>
    <version>11.0.0</version>
</dependency>
```

To set the version before a build:

`mvn versions:set -DnewVersion=11.0.0`

Script regression fixtures: `cfml.parsing/src/test/resources/cfml/tests/modern/`.
