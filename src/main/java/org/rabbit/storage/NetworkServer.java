package org.rabbit.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "network")
@Component
public class NetworkServer {

    private List<SingleConfig> redisConfigs;

    @Data
    public static class SingleConfig {
        private String host;
        private Integer port;
    }
}