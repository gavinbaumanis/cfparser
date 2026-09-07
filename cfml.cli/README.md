# cfml.cli

Native binary CLI for cfparser on the Java 21 line (`21.0.0`). Build the native image with GraalVM:

```
mvn -pl cfml.cli -am package -Pnative
```

Without `-Pnative`, `mvn package` produces the Java 21 CLI jar only.

```xml
<dependency>
    <groupId>com.github.cfmleditor</groupId>
    <artifactId>cfml.cli</artifactId>
    <version>21.0.0</version>
</dependency>
```
