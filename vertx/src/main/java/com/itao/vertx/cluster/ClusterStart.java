package com.itao.vertx.cluster;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClusterStart {

    public static void main(String[] args) {

        HazelcastClusterManager clusterManager = new HazelcastClusterManager();

        VertxOptions options = new VertxOptions().setClusterManager(clusterManager);
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                DeploymentOptions deploymentOptions = new DeploymentOptions();
                vertx.deployVerticle(ReceiverVerticle::new, deploymentOptions, ar -> {
                    if (ar.succeeded()) {
                        String result = ar.result();
                        log.info("ReceiverVerticle deployed success [{}]", result);
                    } else {
                        log.info("erro:{}", ar.cause().getMessage());
                    }
                });
                vertx.deployVerticle(SenderVerticle::new, deploymentOptions,ar -> {
                    if (ar.succeeded()) {
                        String result = ar.result();
                        log.info("SenderVerticle deployed success [{}]", result);
                    } else {
                        log.info("error:{}", ar.cause().getMessage());
                    }
                });
            }
        });
    }
}
