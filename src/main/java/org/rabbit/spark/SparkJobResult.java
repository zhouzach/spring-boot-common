package org.rabbit.spark;

import lombok.Data;

@Data
public class SparkJobResult {
    private String jobId;
    private String status;
    private String result;
}
