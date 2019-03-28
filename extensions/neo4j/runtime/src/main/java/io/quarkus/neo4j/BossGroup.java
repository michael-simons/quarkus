package io.quarkus.neo4j;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * @author Michael J. Simons michael.simons@neo4j.com
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BossGroup {
}
