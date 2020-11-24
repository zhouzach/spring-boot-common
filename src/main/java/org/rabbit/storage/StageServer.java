package org.rabbit.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "stage")
@Component
public class StageServer {

    private List<String> servers = new ArrayList<>();

    public List<String> getServers(){
        return this.servers;
    }
}
