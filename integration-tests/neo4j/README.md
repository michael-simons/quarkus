# Neo4j example

## Running the tests

By default, the tests of this module are disabled.

To run the tests in a standard JVM with Neo4j started as a Docker container, you can run the following command:

```
mvn clean install -Pdocker-neo4j -Ptest-neo4j
```

To also test as a native image, add `-Pnative-image`:

```
mvn clean install -Pdocker-neo4j -Ptest-neo4j -Pnative-image
```

Alternatively you can connect to your own Neo4j instance or cluster.
Reconfigure the connection URL with `-Dneo4j.uri=bolt+routing://yourcluster:7687`;
you'll probably want to change the authentication password too: `-Dneo4j.password=NotS0Secret`.
