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
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
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

//                response.reset();
                try {
                    OutputStream output = response.getOutputStream();

                    ExcelWriter excelWriter = new ExcelWriter();

                    List<Book> listBook = excelWriter.getListBook();
                    val books = listBook.stream().map(ObjectHelper::toMap).collect(Collectors.toList());

                    Workbook workbook = new HSSFWorkbook();
//                    Workbook workbook = new XSSFWorkbook();

                    List<String> headerList = Arrays.asList("title", "author", "price");
//                    excelWriter.write2OutputStream(header, listBook, workbook, "book1", output);

//                    String fileName = URLEncoder.encode("download", "UTF-8");
//                    fileName = URLDecoder.decode(fileName, "ISO8859_1");
                    System.out.println("getContentType: " + response.getContentType());
                    System.out.println("Content-disposition: " + response.getHeader("Content-Disposition"));

                    response.setContentType("application/octet-stream");
                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+"Info1.xls"+"\"");


                    Sheet sheet = workbook.createSheet("sheet1");
                    excelWriter.write2Sheet(sheet, headerList, books);
                    excelWriter.write2OutputStream(workbook, output);
//                    excelWriter.writeData(workbook, sheet, 0, 0, books, output);
                    System.out.println("getContentType: " + response.getContentType());
                    System.out.println("Content-disposition: " + response.getHeader("Content-Disposition"));
//                    OutputStream os = new BufferedOutputStream(response.getOutputStream());

                    output.flush();
                    output.close();
//                    response.setHeader("Content-disposition", "attachment; filename=Info.xls");
//                    response.setContentType("application/msexcel");
//                    output.close();
                }catch (IOException exp) {
                    exp.printStackTrace();
                }
                System.out.println("成功创建excel文件");

    }



}
