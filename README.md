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
                                <version>0.0.4-SNAPSHOT</version>
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
$ java -jar target/jaxrs-doclet-sample-dropwizard-0.0.4-SNAPSHOT.jar server sample.yml
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

## Override Swagger UI

To override the swagger ui included with the doclet, create your own swagger-ui.zip file and add a swaggerUiZipPath to the additionalparam attribute in the pom file.

```
<additionalparam>-apiVersion 1 -docBasePath /apidocs -apiBasePath / -swaggerUiZipPath ../../../src/main/resources/swagger-ui.zip</additionalparam>
```

## Override Model type returned by the REST method

If the REST method returns e.g. `javax.ws.rs.core.Response` wrapping the real response type and the model is not generated, it is possible to override the type that should be used for model generation in a dedicated conf file. 
It's a standard java properties file with mappings:

    qualified method name=java type that should be used for model generation

e.g.

    fixtures.sample.Service.getSubResourceWrappedInResponse(java.lang.String,java.util.List<fixtures.sample.model.SomeClass>)=fixtures.sample.SubResource
    fixtures.sample.Service.otherMethod()=fixtures.sample.SomeOtherClass

Please notice there are NO white characters.
The classes used for overriding MUST belong to the sources processed by this tool.

To activate this feature, `-returnTypesOverrideMapping` doclet option has to be used:

```
<additionalparam>-apiVersion 1 -docBasePath /apidocs -apiBasePath / -returnTypesOverrideMapping PATH_TO_CONF_FILE/CONF_FILE.properties</additionalparam>
```
