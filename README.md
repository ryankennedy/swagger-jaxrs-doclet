# Swagger Doclet

A JavaDoc Doclet that can be used to generate a Swagger resource listing suitable for feeding to
swagger-ui.

## Usage

To use the Swagger Doclet in your Maven project, add the following to your POM file.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>…</groupId>
    <artifactId>…</artifactId>
    <version>…</version>

    <dependencies>
        …
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>2.8</version>
                <executions>
                    <execution>
                        <id>generate-service-docs</id>
                        <phase>generate-resources</phase>
                        <configuration>
                            <doclet>com.yammer.dropwizard.apidocs.ServiceDoclet</doclet>
                            <docletArtifact>
                                <groupId>com.unclehulka</groupId>
                                <artifactId>swagger-jaxrs-doclet</artifactId>
                                <version>0.0.1-SNAPSHOT</version>
                            </docletArtifact>
                            <reportOutputDirectory>${project.build.outputDirectory}</reportOutputDirectory>
                            <useStandardDocletOptions>false</useStandardDocletOptions>
                            <additionalparam>-apiVersion 0</additionalparam>
                            <additionalparam>-apiBasePath http://localhost:8080</additionalparam>
                            <additionalparam>-docBasePath http://localhost:8080</additionalparam>
                        </configuration>
                        <goals>
                            <goal>javadoc</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</xml>
```
