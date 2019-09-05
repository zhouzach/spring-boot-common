package org.rabbit.spark;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class SparkJobServerConfig {

    @Value("${spark.master}")
    private String sparkMaster;

    @Value("${spark.job.jar}")
    private String sparkJobJar;

    @Value("${spark.app.name}")
    private String sparkAppName;
}
