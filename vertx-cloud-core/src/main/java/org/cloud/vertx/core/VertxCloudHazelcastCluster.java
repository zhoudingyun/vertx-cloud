package org.cloud.vertx.core;

import io.netty.util.internal.StringUtil;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.apache.commons.io.FileUtils;
import org.cloud.vertx.core.util.IpUtils;
import org.cloud.vertx.core.util.VertxCloudUtils;

import java.io.File;
import java.util.Objects;

public class VertxCloudHazelcastCluster {
    private static Logger LOGGER;

    private static void init() {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
        System.setProperty("hazelcast.logging.type", "log4j2");
        LOGGER = LoggerFactory.getLogger(VertxCloudHazelcastCluster.class);
    }

    public static Future<Vertx> run(String[] args) {
        Promise<Vertx> promise = Promise.promise();
        init();
        try {
            LOGGER.info("Load The vertx config from " + System.getProperty("vertx-config-path"));

            Vertx bootstrapVertx = Vertx.vertx();
            ConfigRetriever bootstrapRetriever = ConfigRetriever.create(bootstrapVertx);
            bootstrapRetriever.getConfig().onSuccess(bootstrapConf -> {
                LOGGER.info("The vertx bootstrap conf:" + bootstrapConf.toString());

                JsonObject microservicesConfig = VertxCloudUtils.checkConfig(bootstrapConf, "vertx.cloud.microservices.config");
                // 从配置中心获取配置
                ConfigRetriever retriever = null;
                // 如果配置中心使用git
                if (microservicesConfig.containsKey("git")) {
                    JsonObject gitConfig = microservicesConfig.getJsonObject("git", null);
                    Objects.requireNonNull(gitConfig, "the property vertx.cloud.microservices.config.git is not configured, please check config.json.");
                    ConfigStoreOptions gitConfigStoreOptions = new ConfigStoreOptions()
                            .setType("git")
                            .setConfig(new JsonObject()
                                    .put("url", gitConfig.getString("url"))
                                    .put("path", gitConfig.getString("path"))
                                    .put("branch", gitConfig.getString("branch"))
                                    .put("remote", gitConfig.getString("remote"))
                                    .put("user", gitConfig.getString("user"))
                                    .put("password", gitConfig.getString("password"))
                                    .put("filesets", gitConfig.getJsonArray("filesets")));

                    retriever = ConfigRetriever.create(bootstrapVertx, new ConfigRetrieverOptions().addStore(gitConfigStoreOptions));
                }

                if (retriever != null) {
                    // Load configuration file from configuration center
                    retriever.getConfig().onSuccess(config -> {
                        // Close bootstrap certx
                        bootstrapVertx.close();

                        // Get VertxOptions
                        JsonObject optionsConfig = VertxCloudUtils.checkConfig(config, "vertx.options");
                        // Set VertxOptions
                        VertxOptions vertxOptions;
                        if (optionsConfig != null) {
                            vertxOptions = new VertxOptions(optionsConfig);
                        } else {
                            vertxOptions = new VertxOptions();
                        }

                        // 获取计算机ip地址
                        String localHost = IpUtils.getLocalHost();
                        String host = localHost;
                        String clusterPublicHost = localHost;
                        // Get EventBusOptions
                        JsonObject eventbusOptionsConfig = VertxCloudUtils.checkConfig(config, "vertx.eventbus.options");
                        if (eventbusOptionsConfig != null) {
                            if (eventbusOptionsConfig.containsKey("clusterPublicHost")) {
                                clusterPublicHost = eventbusOptionsConfig.getString("clusterPublicHost");
                            }
                            if (eventbusOptionsConfig.containsKey("host")) {
                                host = eventbusOptionsConfig.getString("host");
                            }
                        }
                        // Set EventBusOptions
                        EventBusOptions eventBusOptions = new EventBusOptions().setClusterPublicHost(clusterPublicHost).setHost(host);
                        vertxOptions.setEventBusOptions(eventBusOptions);

                        createVertx(config, vertxOptions).onComplete(promise);
                    }).onFailure(throwable -> {
                        bootstrapVertx.close();
                        promise.fail(throwable);
                    });
                } else {
                    promise.fail(new Exception("not found microservices config file"));
                }
            }).onFailure(throwable -> {
                promise.fail(throwable);
            });
        } catch (Throwable throwable) {
            promise.fail(throwable);
        }

        return promise.future();
    }

    public static Future<Vertx> createVertx(final JsonObject config, final VertxOptions options) {
        //Get clustering config
        JsonObject clusteringConfig = VertxCloudUtils.checkConfig(config, "vertx.cloud.clustering");
        if (clusteringConfig != null) {
            ClusterManager clusterManager = null;
            String clusteringConfigPath = clusteringConfig.getString("config");
            if (StringUtil.isNullOrEmpty(clusteringConfigPath)) {
                return Future.failedFuture("the property vertx.cloud.clustering.config is not configured, please check config.json.");
            }

            File clusteringConfigFile = FileUtils.getFile(clusteringConfigPath);
            if (clusteringConfigFile.exists()) {
                clusteringConfigPath = clusteringConfigFile.getAbsolutePath();
            } else {
                return Future.failedFuture("not found vertx.cloud.clustering.config file");
            }
            System.setProperty("vertx.hazelcast.config", clusteringConfigPath);
            clusterManager = new HazelcastClusterManager();

            VertxPrometheusOptions vertxPrometheusOptions = new VertxPrometheusOptions().setEnabled(true);
            MetricsOptions metricsOptions = new MicrometerMetricsOptions().setPrometheusOptions(vertxPrometheusOptions).setEnabled(true);
            options.setMetricsOptions(metricsOptions);
            options.setClusterManager(clusterManager);
            Promise<Vertx> promise = Promise.promise();

            Vertx.clusteredVertx(options).onSuccess(vertx -> {
                promise.complete(vertx);
            }).onFailure(throwable -> {
                promise.fail(throwable);
            });

            return promise.future();
        } else {
            return Future.failedFuture("the property vertx.cloud.clustering is not configured, please check config.json.");
        }
    }

}
