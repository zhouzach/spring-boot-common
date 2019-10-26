package org.rabbit.controller;


import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.rabbit.module.Book;
import org.rabbit.module.Msg;
import org.rabbit.utils.ExcelWriter;
import org.rabbit.utils.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ExportController {


    protected Logger logger = LoggerFactory.getLogger(getClass());



    @GetMapping("")
    public Msg getAllLoadBehavior(HttpServletRequest request, HttpServletResponse response) {
        logger.info("hello spring boot");

        Msg msg = Msg.ok("hello spring boot");


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
