package io.quarkus.neo4j.runtime;

import java.util.Optional;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Template;

/**
 * @author Michael J. Simons
 */
@Template
public class Neo4jDriverTemplate {

    static volatile Driver driver;

    public void configureNeo4jProducer(BeanContainer beanContainer, Neo4jDriverConfiguration configuration,
            ShutdownContext shutdownContext) {

        if (driver == null) {
            initializeDriver(configuration, shutdownContext);
        }

        Neo4jDriverProducer driverProducer = beanContainer.instance(Neo4jDriverProducer.class);
        driverProducer.initialize(driver);
    }

    private void initializeDriver(Neo4jDriverConfiguration configuration, ShutdownContext shutdownContext) {
        AuthToken authToken;
        String uri;

        if (configuration == null) {
            uri = Neo4jDriverConfiguration.DEFAULT_SERVER_URI;
            authToken = AuthTokens.none();
        } else {
            uri = configuration.uri;
            authToken = Optional.ofNullable(configuration.authentication)
                    .map(authentication -> AuthTokens.basic(authentication.username, authentication.password))
                    .orElseGet(AuthTokens::none);

        }

        driver = GraphDatabase.driver(uri, authToken);
        shutdownContext.addShutdownTask(driver::close);
    }
}
