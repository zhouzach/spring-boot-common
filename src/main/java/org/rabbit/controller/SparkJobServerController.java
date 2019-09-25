package org.rabbit.controller;

import org.rabbit.module.Msg;
import org.rabbit.spark.SparkJobResult;
import org.rabbit.spark.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/spark/job")
public class SparkJobServerController {

    @Autowired
    SubmitService submitService;

    @GetMapping("/get/result/{jobId}")
    public SparkJobResult getSnappyJobResult(@PathVariable String jobId) {

        try {
            com.bluebreezecf.tools.sparkjobserver.api.SparkJobResult result = submitService.getJobResult(jobId);
            SparkJobResult snappyJobResult = new SparkJobResult();
            snappyJobResult.setJobId(result.getJobId());
            snappyJobResult.setStatus(result.getStatus());
            snappyJobResult.setResult(result.toString());

            return snappyJobResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping("/submit")
    public Msg calculateIndexAsync(@RequestBody Map<String, String> req) {
        System.out.println("http post: /spark/job/submit");

        String indexIds = req.getOrDefault("index_ids", "rabbit");
        String indexStatus = req.get("index_status");
        String begin = req.get("begin");
        String end = req.get("end");

        StringBuilder dateParam = new StringBuilder();

        dateParam.append("index_status=").append(indexStatus);


        if (indexIds == null || "".equals(indexIds)) {
            dateParam.append(",index_ids=rabbit");
        } else {
            dateParam.append(",index_ids=").append(indexIds);
        }


        if (begin == null || "".equals(begin)) {
            dateParam.append(",begin=rabbit");
        } else {
            dateParam.append(",begin=").append(begin);
        }

        if (end == null || "".equals(end)) {
            dateParam.append(",end=rabbit");
        } else {
            dateParam.append(",end=").append(end);
        }

        return submitService.pollingAsyncExecuteResult("org.rabbit.execJob", dateParam.toString());

    }


}
