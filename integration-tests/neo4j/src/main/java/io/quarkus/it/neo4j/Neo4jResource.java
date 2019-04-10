package io.quarkus.it.neo4j;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.StatementResultCursor;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;

/**
 * @author Michael J. Simons
 */
@Path("/neo4j/testfunctionality")
public class Neo4jResource {

    @Inject
    Driver driver;

    @GET
    public String doStuffWithNeo4j() {
        try {
            createNodes(driver);

            readNodes(driver);

            readNodesAsync(driver);
        } catch (Exception e) {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            reportException("An error occurred while performing Neo4j operations", e, writer);
            writer.flush();
            writer.close();
            return out.toString();
        }
        return "OK";
    }

    private static void createNodes(Driver driver) {
        try (Session session = driver.session();
                Transaction transaction = session.beginTransaction()) {
            transaction.run("CREATE (f:Framework {name: $name}) - [:CAN_USE] -> (n:Database {name: 'Neo4j'})",
                    Values.parameters("name", "Quarkus"));
            transaction.success();
        }
    }

    private static void readNodes(Driver driver) {
        try (Session session = driver.session();
                Transaction transaction = session.beginTransaction()) {
            StatementResult result = transaction
                    .run("MATCH (f:Framework {name: $name}) - [:CAN_USE] -> (n) RETURN f, n",
                            Values.parameters("name", "Quarkus"));
            result.forEachRemaining(
                    record -> System.out.println(String.format("%s works with %s", record.get("n").get("name").asString(),
                            record.get("f").get("name").asString())));
            transaction.success();
        }
    }

    private static void readNodesAsync(Driver driver) {
        try (Session session = driver.session()) {
            session.runAsync("UNWIND range(1, 3) AS x RETURN x")
                    .thenCompose(StatementResultCursor::listAsync)
                    .whenComplete((records, error) -> {
                        if (records != null) {
                            System.out.println(records);
                        } else {
                            error.printStackTrace();
                        }
                    });
        }
    }

    private void reportException(String errorMessage, final Exception e, final PrintWriter writer) {
        if (errorMessage != null) {
            writer.write(errorMessage);
            writer.write(" ");
        }
        writer.write(e.toString());
        writer.append("\n\t");
        e.printStackTrace(writer);
        writer.append("\n\t");
    }
}
