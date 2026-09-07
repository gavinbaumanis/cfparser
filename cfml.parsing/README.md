# cfml.parsing

[![Build Status](https://travis-ci.org/cfparser/cfparser.svg?branch=java-11)](https://travis-ci.org/cfparser/cfparser)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b25fc5beacea4d4f9c493971fcfb7e90)](https://www.codacy.com/app/ryaneberly/cfparser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cfparser/cfparser&amp;utm_campaign=Badge_Grade)

CFML parser for the Java 11 line (`11.0.0`). Uses `cfml.dictionary` for engine-specific tag and function metadata.

On Maven Central. License: BSD (<http://www.opensource.org/licenses/bsd-license.html>).

Build:

```
mvn clean install
```

```xml
<dependency>
    <groupId>com.github.cfmleditor</groupId>
    <artifactId>cfml.parsing</artifactId>
    <version>11.0.0</version>
</dependency>
```

Modern script fixtures: `src/test/resources/cfml/tests/modern/`.
Parameterized tests: `cfml.parsing.TestFiles`. Arrow AST: `cfml.parsing.TestLambdaAst`.
