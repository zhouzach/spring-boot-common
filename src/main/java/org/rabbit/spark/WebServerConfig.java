package org.rabbit.spark;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class WebServerConfig {

    @Value("${spark.job.result.url}")
    private String sparkJobResultUrl;

}
