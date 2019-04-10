package io.quarkus.it.neo4j;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * Test connecting via Neo4j Java-Driver to Neo4j.
 * Can quickly start a matching database with:
 *
 * <pre>
 *     docker run --publish=7474:7474 --publish=7687:7687 -e 'NEO4J_AUTH=neo4j/music' neo4j:3.5.3
 * </pre>
 *
 * @author Michael J. Simons
 */
@QuarkusTest
public class Neo4jFunctionalityTest {

    @Test
    public void testNeo4jFunctionalityFromServlet() {
        RestAssured.given().when().get("/neo4j/testfunctionality").then().body(is("OK"));
    }

}
