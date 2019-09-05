package org.rabbit.spark;

import com.bluebreezecf.tools.sparkjobserver.api.*;
import com.fasterxml.uuid.Generators;
import org.joda.time.DateTime;
import org.rabbit.module.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SubmitService {

    @Autowired
    SparkJobServerConfig snappyJobConfig;
    @Autowired
    WebServerConfig serverConfig;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public com.bluebreezecf.tools.sparkjobserver.api.SparkJobResult execute(String classPath, String classParams,
                                                                            String isSync, String syncTimeOut) throws Exception {
        System.out.println("param data:" + classParams);
        logger.info("param data:" + classParams);

        ISparkJobServerClient client = null;
        try {
            client = SparkJobServerClientFactory.getInstance()
                    .createSparkJobServerClient(snappyJobConfig.getSparkMaster());
            System.out.println("Spark Job Server: " + snappyJobConfig.getSparkMaster());
            logger.info("Spark Job Server: " + snappyJobConfig.getSparkMaster());

            String dt = new DateTime().toString("yyyy-MM-dd_HH-mm-ss-sss");
            String appName = snappyJobConfig.getSparkAppName();
            UUID uuid = Generators.timeBasedGenerator().generate();
            String contextName = "cxt_" + appName + "_" + dt + "_" + uuid;

            //POST /contexts/<name>--Create context with parameters
            Map<String, String> clientConfig = new HashMap<String, String>();
            clientConfig.put(ISparkJobServerClientConstants.PARAM_MEM_PER_NODE, "8g");
            clientConfig.put(ISparkJobServerClientConstants.PARAM_NUM_CPU_CORES, "20");

            // maximum delay is [21474835] seconds   2592000 = 60 * 60 * 24 * 30
//            clientConfig.put(ISparkJobServerClientConstants.PARAM_TIMEOUT, "2592000");
            clientConfig.put(ISparkJobServerClientConstants.PARAM_TIMEOUT, syncTimeOut);
            client.createContext(contextName, clientConfig);


            //Post /jobs---Create a new job
            clientConfig.put(ISparkJobServerClientConstants.PARAM_APP_NAME, appName);
            clientConfig.put(ISparkJobServerClientConstants.PARAM_CLASS_PATH, classPath);
            //1.start a spark job asynchronously and just get the status information

            //2.start a spark job synchronously and wait until the result
            clientConfig.put(ISparkJobServerClientConstants.PARAM_CONTEXT, contextName);
//            clientConfig.put(ISparkJobServerClientConstants.PARAM_SYNC, "true");
            clientConfig.put(ISparkJobServerClientConstants.PARAM_SYNC, isSync);
            com.bluebreezecf.tools.sparkjobserver.api.SparkJobResult result;
            result = client.startJob(classParams, clientConfig);

            logger.info("SparkJobResult: " + result);



            return result;
        } catch (Exception e1) {
            logger.info(e1.getMessage());
            e1.printStackTrace();
            throw e1;
        }
    }

    public com.bluebreezecf.tools.sparkjobserver.api.SparkJobResult asyncExecute(String classPath, String data) throws Exception {
        return execute(classPath, data, "false", "2592000");
    }

//    public SparkJobResult syncExecute(String classPath, String data) throws Exception {
//        return execute(classPath, data, "true", "2592000");
//    }


    public Msg pollingAsyncExecuteResult(String className, String param) {
        try {
            com.bluebreezecf.tools.sparkjobserver.api.SparkJobResult result = asyncExecute(className, param);
            String jobId = result.getJobId();

            RestTemplate restTemplate = new RestTemplate();
            String url = serverConfig.getSparkJobResultUrl() + "/" + jobId;
            SparkJobResult sparkJobResult = restTemplate.getForObject(url, SparkJobResult.class);
            assert sparkJobResult != null;
            String status = sparkJobResult.getStatus();
            while (!"FINISHED".equals(status) && !"ERROR".equals(status)) {
                Thread.sleep(1000);

                sparkJobResult = restTemplate.getForObject(url, SparkJobResult.class);
                status = sparkJobResult.getStatus();
            }
            logger.info("SparkJobResult: " + sparkJobResult);
            if ("FINISHED".equals(status)) return Msg.ok(sparkJobResult);
            else return Msg.err(sparkJobResult);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return Msg.err(e.getMessage());
        }

    }


    public boolean stop(String contextName) throws Exception {
        logger.info("contextName:" + contextName);
        System.out.println("contextName:" + contextName);

        ISparkJobServerClient client = null;
        try {
            client = SparkJobServerClientFactory.getInstance()
                    .createSparkJobServerClient(snappyJobConfig.getSparkMaster());
            logger.info("Spark Job Server: " + snappyJobConfig.getSparkMaster());


            boolean b = client.deleteContext(contextName);


            return b;
        } catch (Exception e1) {
            logger.error(e1.getMessage());
            e1.printStackTrace();
            throw e1;
        }
    }

    public List<SparkJobInfo> list() throws Exception {

        ISparkJobServerClient client = null;
        try {
            client = SparkJobServerClientFactory.getInstance()
                    .createSparkJobServerClient(snappyJobConfig.getSparkMaster());
            logger.info("Spark Job Server: " + snappyJobConfig.getSparkMaster());


            //GET /jobs
            List<SparkJobInfo> jobInfos = client.getJobs();
            logger.info("Current jobs:");
            for (SparkJobInfo jobInfo : jobInfos) {
                System.out.println(jobInfo);
            }


            return jobInfos;
        } catch (Exception e1) {
            logger.error(e1.getMessage());
            e1.printStackTrace();
            throw e1;
        }
    }

    public com.bluebreezecf.tools.sparkjobserver.api.SparkJobResult getJobResult(String jobId) throws Exception {

        ISparkJobServerClient client = null;
        try {
            client = SparkJobServerClientFactory.getInstance()
                    .createSparkJobServerClient(snappyJobConfig.getSparkMaster());
            return client.getJobResult(jobId);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw e1;
        }
    }

    public String uploadJobJar() throws Exception {

        ISparkJobServerClient client = null;
        try {
            client = SparkJobServerClientFactory.getInstance()
                    .createSparkJobServerClient(snappyJobConfig.getSparkMaster());
            logger.info("Spark Job Server: " + snappyJobConfig.getSparkMaster());

            String jobJar = snappyJobConfig.getSparkJobJar();
            String appName = snappyJobConfig.getSparkAppName();

            //upload /jars/<appName>
            logger.info("jobJar: " + jobJar);
            logger.info("appName: " + appName);
            boolean b = client.uploadSparkJobJar(new File(jobJar), appName);

            String res;

            if (b) res = "job jar upload successfully!";
            else res = " job jar fail to upload!";

            return res;
        } catch (Exception e1) {
            logger.error(e1.getMessage());
            e1.printStackTrace();
            throw e1;
        }
    }

}
