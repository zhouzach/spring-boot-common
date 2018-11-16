package org.rabbit.controller;


import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rabbit.module.Book;
import org.rabbit.module.Msg;
import org.rabbit.utils.ExcelWriter;
import org.rabbit.utils.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class HelloController {


    protected Logger logger = LoggerFactory.getLogger(getClass());



    @GetMapping("")
    public Msg getAllLoadBehavior(HttpServletRequest request, HttpServletResponse response) {
        logger.info("hello spring boot");

        Msg msg = Msg.ok("hello spring boot");


        return msg;
    }


    @GetMapping("/export")
    public void exportInfo(HttpServletResponse response){

//                response.reset();
                try {
                    OutputStream output = response.getOutputStream();

                    ExcelWriter excelWriter = new ExcelWriter();

                    List<Book> listBook = excelWriter.getListBook();
                    val books = listBook.stream().map(ObjectHelper::toMap).collect(Collectors.toList());

//                    Workbook workbook = new HSSFWorkbook();
                    Workbook workbook = new XSSFWorkbook();

                    List<String> header = Arrays.asList("title", "author", "price");
//                    excelWriter.write2OutputStream(header, listBook, workbook, "book1", output);
                    excelWriter.write2OutputStream(header, books, workbook, "book1", output);

//                    String fileName = URLEncoder.encode("download", "UTF-8");
//                    fileName = URLDecoder.decode(fileName, "ISO8859_1");
//                    System.out.println("getContentType: " + response.getContentType());
//                    System.out.println("Content-disposition: " + response.getHeader("Content-disposition"));

                    response.setContentType("application/octet-stream");
                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Info.xls");


                    System.out.println("getContentType: " + response.getContentType());
                    System.out.println("Content-disposition: " + response.getHeader("Content-disposition"));
//                    response.setHeader("Content-disposition", "attachment; filename=Info.xls");
//                    response.setContentType("application/msexcel");
                    output.close();
                }catch (IOException exp) {
                    exp.printStackTrace();
                }
                System.out.println("成功创建excel文件");

    }



}
