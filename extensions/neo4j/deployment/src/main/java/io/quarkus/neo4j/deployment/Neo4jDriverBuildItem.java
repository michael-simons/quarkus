package io.quarkus.neo4j.deployment;

import org.neo4j.driver.Driver;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.runtime.RuntimeValue;

/**
 * @author Michael J. Simons
 */
public final class Neo4jDriverBuildItem extends SimpleBuildItem {

    private final RuntimeValue<Driver> driver;

    public Neo4jDriverBuildItem(RuntimeValue<Driver> driver) {
        this.driver = driver;
    }

    public RuntimeValue<Driver> getDriver() {
        return driver;
    }
}
