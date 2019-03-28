/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.neo4j.deployment;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.neo4j.driver.internal.shaded.io.netty.channel.EventLoopGroup;

import io.quarkus.arc.deployment.RuntimeBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.substrate.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.substrate.SubstrateConfigBuildItem;
import io.quarkus.neo4j.BossGroup;
import io.quarkus.neo4j.runtime.NettyTemplate;

/**
 * @author Michael J. Simons michael.simons@neo4j.com
 */
class NettyProcessor {

    @Inject
    BuildProducer<ReflectiveClassBuildItem> reflectiveClass;

    private static final Logger log = Logger.getLogger(NettyProcessor.class);

    @BuildStep
    SubstrateConfigBuildItem build() {
        reflectiveClass.produce(new ReflectiveClassBuildItem(false, false,
                "org.neo4j.driver.internal.shaded.io.netty.channel.socket.nio.NioSocketChannel"));
        reflectiveClass
                .produce(new ReflectiveClassBuildItem(false, false,
                        "org.neo4j.driver.internal.shaded.io.netty.channel.socket.nio.NioServerSocketChannel"));
        reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, "java.util.LinkedHashMap"));

        SubstrateConfigBuildItem.Builder builder = SubstrateConfigBuildItem.builder()
                .addNativeImageSystemProperty("io.netty.noUnsafe", "true")
                .addNativeImageSystemProperty("io.netty.leakDetection.level", "DISABLED")
                .addRuntimeInitializedClass(
                        "org.neo4j.driver.internal.shaded.io.netty.handler.ssl.JdkNpnApplicationProtocolNegotiator")
                .addRuntimeInitializedClass(
                        "org.neo4j.driver.internal.shaded.io.netty.handler.ssl.ReferenceCountedOpenSslEngine")
                .addRuntimeInitializedClass(
                        "org.neo4j.driver.internal.shaded.io.netty.handler.ssl.util.ThreadLocalInsecureRandom");
        try {
            Class.forName("org.neo4j.driver.internal.shaded.io.netty.handler.codec.http.HttpObjectEncoder");
            builder.addRuntimeReinitializedClass("org.neo4j.driver.internal.shaded.io.netty.handler.codec.http2.Http2CodecUtil")
                    .addRuntimeInitializedClass(
                            "org.neo4j.driver.internal.shaded.io.netty.handler.codec.http.HttpObjectEncoder")
                    .addRuntimeInitializedClass(
                            "org.neo4j.driver.internal.shaded.io.netty.handler.codec.http2.DefaultHttp2FrameWriter")
                    .addRuntimeInitializedClass(
                            "org.neo4j.driver.internal.shaded.io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder");
        } catch (ClassNotFoundException e) {
            //ignore
            log.debug("Not registering Netty HTTP classes as they were not found");
        }
        return builder //TODO: make configurable
                .build();
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void createExecutors(BuildProducer<RuntimeBeanBuildItem> runtimeBeanBuildItemBuildProducer,
            NettyTemplate template) {
        //TODO: configuration
        Supplier<Object> boss = template.createEventLoop(1);
        Supplier<Object> worker = template.createEventLoop(0);

        runtimeBeanBuildItemBuildProducer.produce(RuntimeBeanBuildItem.builder(EventLoopGroup.class)
                .setSupplier(boss)
                .setScope(ApplicationScoped.class)
                .addQualifier(BossGroup.class)
                .build());
        runtimeBeanBuildItemBuildProducer.produce(RuntimeBeanBuildItem.builder(EventLoopGroup.class)
                .setSupplier(worker)
                .setScope(ApplicationScoped.class)
                .build());
    }

}
