## Installation



[Maven](http://maven.apache.org/) 2.x+ is required to build the project.

1.  Install the console package.

        mvn install -P local

	or

        mvn install -P local,replicate

    The optional `replicate` profile activates the deployed package to the local publish instance.

2.  [Verify](http://localhost:4502/groovyconsole) the installation.

Additional build profiles may be added in the project's pom.xml to support deployment to non-localhost CQ5 servers.

## Context Path Support

If you are running AEM with a context path, set the Maven property `cq.context.path` during installation.

    mvn install -P local -Dcq.context.path=/context
