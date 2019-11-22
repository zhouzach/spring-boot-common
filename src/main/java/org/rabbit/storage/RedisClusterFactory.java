package org.rabbit.storage;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
public class RedisClusterFactory {

    @Data
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "spring.cache.redis.cluster")
    public static class RedisCluster {
        private Server server1;
        private Server server2;
    }

    @Data
    public static class Server {
        private String host;
        private Integer port;
    }

    @Autowired
    private RedisCluster servers;

    public RedisCluster getRedisCluster(){
        return servers;
    }


}
