# Swagger Doclet [![Build Status](https://travis-ci.org/ryankennedy/swagger-jaxrs-doclet.png)](https://travis-ci.org/ryankennedy/swagger-jaxrs-doclet)

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
    
    <repositories>
        <repository>
            <id>com.hypnoticocelot</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

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
                            <doclet>com.hypnoticocelot.jaxrs.doclet.ServiceDoclet</doclet>
                            <docletArtifact>
                                <groupId>com.hypnoticocelot</groupId>
                                <artifactId>jaxrs-doclet</artifactId>
                                <version>0.0.2-SNAPSHOT</version>
                            </docletArtifact>
                            <reportOutputDirectory>${project.build.outputDirectory}</reportOutputDirectory>
                            <useStandardDocletOptions>false</useStandardDocletOptions>
                            <additionalparam>-apiVersion 1 -docBasePath ${server.url}/apidocs -apiBasePath ${server.url}</additionalparam>
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
