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
    
    <dependencies>
        …
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>2.9.1</version>
                <executions>
                    <execution>
                        <id>generate-service-docs</id>
                        <phase>generate-resources</phase>
                        <configuration>
                            <doclet>com.hypnoticocelot.jaxrs.doclet.ServiceDoclet</doclet>
                            <docletArtifact>
                                <groupId>com.hypnoticocelot</groupId>
                                <artifactId>jaxrs-doclet</artifactId>
                                <version>0.0.2</version>
                            </docletArtifact>
                            <reportOutputDirectory>${project.build.outputDirectory}</reportOutputDirectory>
                            <useStandardDocletOptions>false</useStandardDocletOptions>
                            <additionalparam>-apiVersion 1 -docBasePath /apidocs -apiBasePath /</additionalparam>
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

## Example

An example project using Dropwizard is included in `jaxrs-doclet-sample-dropwizard`. To get it running, run the following commands.

```
$ cd jaxrs-doclet-sample-dropwizard
$ mvn package
$ java -jar target/jaxrs-doclet-sample-dropwizard target/jaxrs-doclet-sample-dropwizard-0.0.2-SNAPSHOT.jar sample.yml
```

The example server should be running on port 8080:

```
$ curl localhost:8080/apidocs/service.json
{
  "apiVersion" : "1",
  "basePath" : "/apidocs/",
  "apis" : [ {
    "path" : "/Auth.{format}",
    "description" : ""
  }, {
    "path" : "/HttpServletRequest.{format}",
    "description" : ""
  }, {
    "path" : "/ModelResource_modelid.{format}",
    "description" : ""
  }, {
    "path" : "/Recursive.{format}",
    "description" : ""
  }, {
    "path" : "/Response.{format}",
    "description" : ""
  }, {
    "path" : "/greetings_name.{format}",
    "description" : ""
  } ],
  "swaggerVersion" : "1.1"
}
$
```
