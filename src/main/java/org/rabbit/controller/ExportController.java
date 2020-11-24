package org.rabbit.controller;


import lombok.val;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.rabbit.module.Book;
import org.rabbit.module.Msg;
import org.rabbit.storage.NetworkServer;
import org.rabbit.storage.RedisClusterFactory;
import org.rabbit.storage.StageServer;
import org.rabbit.utils.ExcelWriter;
import org.rabbit.utils.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ExportController {


    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisClusterFactory redisClusterFactory;

    @Autowired
    StageServer stageServer;

    @Autowired
    NetworkServer networkServer;


    @GetMapping("/redis")
    public Msg getRedis(HttpServletRequest request, HttpServletResponse response) {

        RedisClusterFactory.Server server1 = redisClusterFactory.getRedisCluster().getServer1();
        RedisClusterFactory.Server server2 = redisClusterFactory.getRedisCluster().getServer1();

        Msg msg = Msg.ok(server1 + ", " + server2);


        return msg;
    }

    @GetMapping("/stage")
    public Msg getStageServer(HttpServletRequest request, HttpServletResponse response) {

        Msg msg = Msg.ok(stageServer.getServers());

        return msg;
    }

    @GetMapping("/network")
    public Msg getNetworkServer(HttpServletRequest request, HttpServletResponse response) {

        Msg msg = Msg.ok(networkServer.getRedisConfigs());

        return msg;
    }


    @GetMapping("/export/excel")
    public void exportInfo(HttpServletResponse response){

                try {

                    ExcelWriter excelWriter = new ExcelWriter();

                    List<Book> listBook = excelWriter.getListBook();
                    val books = listBook.stream().map(ObjectHelper::toMap).collect(Collectors.toList());
                    List<String> headerList = Arrays.asList("title", "author", "price");
//                    List<String> headerList = new ArrayList<>();


                    String filename = "Info1.xlsx";

                    response.setContentType("application/vnd.ms-excel");
                    response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(filename, "utf-8"));
                    OutputStream outputStream = response.getOutputStream();

                     Workbook workbook = excelWriter.getWorkbookByFilename(filename);
                    Sheet sheet1 = excelWriter.createSheetByName(workbook, "sheet1");
                    excelWriter.write2Sheet(sheet1, headerList, books);
//                    excelWriter.write2SheetWithMergedCellHeader(sheet1, new HashMap<String, List<String>>(), books);

                    excelWriter.write2OutputStream(workbook, outputStream);

                    outputStream.flush();
                    outputStream.close();
                }catch (IOException exp) {
                    exp.printStackTrace();
                }
                System.out.println("成功创建excel文件");

    }



}
