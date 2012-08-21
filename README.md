# Swagger Doclet

A JavaDoc Doclet that can be used to generate a Swagger resource listing suitable for feeding to
swagger-ui.

## Usage

To use the Swagger Doclet in your Maven project, add the following to your POM file.

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
                        </configuration>
                        <goals>
                            <goal>javadoc</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
