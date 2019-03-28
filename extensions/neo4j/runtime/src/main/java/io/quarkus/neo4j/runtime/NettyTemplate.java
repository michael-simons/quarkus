package io.quarkus.neo4j.runtime;

import java.util.function.Supplier;

import org.neo4j.driver.internal.shaded.io.netty.channel.EventLoopGroup;
import org.neo4j.driver.internal.shaded.io.netty.channel.nio.NioEventLoopGroup;

import io.quarkus.runtime.annotations.Template;

/**
 * @author Michael J. Simons michael.simons@neo4j.com
 */
@Template
public class NettyTemplate {

    public Supplier<Object> createEventLoop(int nThreads) {
        return new Supplier<Object>() {

            volatile EventLoopGroup val;

            @Override
            public EventLoopGroup get() {
                if (val == null) {
                    synchronized (this) {
                        if (val == null) {
                            val = new NioEventLoopGroup(nThreads);
                        }
                    }
                }
                return val;
            }
        };
    }
}
