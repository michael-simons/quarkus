package io.quarkus.neo4j.deployment;

import org.neo4j.driver.v1.Driver;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.neo4j.runtime.Neo4jDriverConfiguration;
import io.quarkus.neo4j.runtime.Neo4jDriverProducer;
import io.quarkus.neo4j.runtime.Neo4jDriverTemplate;
import io.quarkus.runtime.RuntimeValue;

/**
 * @author Michael J. Simons
 */
class Neo4jDriverProcessor {

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FeatureBuildItem.NEO4J);
    }

    @BuildStep
    AdditionalBeanBuildItem registerBean() {
        return new AdditionalBeanBuildItem(false, Neo4jDriverProducer.class);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    Neo4jDriverBuildItem build(Neo4jDriverTemplate template, BeanContainerBuildItem beanContainer,
            ShutdownContextBuildItem shutdownContext,
            Neo4jDriverConfiguration configuration) {

        RuntimeValue<Driver> driver = template
                .configureDriver(beanContainer.getValue(), configuration, shutdownContext);
        return new Neo4jDriverBuildItem(driver);
    }
}
