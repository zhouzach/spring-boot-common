package org.rabbit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rabbit.module.Book;
import org.rabbit.module.People;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelWriter {


    public void writeData(
            Workbook workbook, Sheet sheet,
            Integer x, Integer y,
            List<Map<String, Object>> data,
            OutputStream outputStream) {

        for (Map<String, Object> map : data) {

            int n = ++x;
            Row row = sheet.getRow(n);
            if (row == null) {
                row = sheet.createRow(n);
            }

            setCellValue(y, map, row);
        }

    }

    private void setCellValue(Integer y, Map<String, Object> map, Row row) {
        Object[] keys = map.keySet().toArray();
        for (int k = 0; k < keys.length; k++, y++) {
            row.createCell(y)
                    .setCellValue(String.valueOf(map.get(keys[k])));
        }
    }


    private CellStyle createHeaderStyle(Sheet sheet) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();

        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);

        cellStyle.setFont(font);

        return cellStyle;
    }

    private void createHeaderRow(Sheet sheet, int x, int y, List<String> headerList) {

        Row row = sheet.getRow(x);
        if (row == null) {
            row = sheet.createRow(x);
        }
        CellStyle cellStyle = createHeaderStyle(sheet);

        for (int k = 0; k < headerList.size(); y++, k++) {
            Cell cellTitle = row.createCell(y);
            cellTitle.setCellStyle(cellStyle);
            cellTitle.setCellValue(headerList.get(k));
        }
    }

    /**
     * Creates new cell range. Indexes are zero-based.
     *
     * @param firstRow Index of first row
     * @param lastRow  Index of last row (inclusive), must be equal to or larger than {@code firstRow}
     * @param firstCol Index of first column
     * @param lastCol  Index of last column (inclusive), must be equal to or larger than {@code firstCol}
     */
    private void createMergedCell(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, String value) {
        // 1.创建一个合并单元格
        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
//        CellRangeAddress region = CellRangeAddress.valueOf("A1:E10");
        sheet.addMergedRegion(region);

        // 2.设置合并单元格内容
        System.out.println(firstRow);
        System.out.println(firstCol);
        System.out.println(value);
        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);

        // 设置单元格内容水平垂直居中
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);

        // 3.设置合并单元格边框
        setBorderStyle(sheet, region);
    }

    private void write2WorkbookWithMergedCellHeader(Workbook workbook, Sheet sheet, int firstRow, int lastRow, int firstCol,
                                                    Map<String, List<String>> headerMap,
                                                    Map<String, List<Map<String, Object>>> dataMap,
                                                    OutputStream outputStream) {
        int lastCol = 0;
        val keys = headerMap.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String mergedHeader = String.valueOf(keys[i]);
            List<String> headerList = headerMap.get(keys[i]);

            lastCol = firstCol + headerList.size() - 1;
            createMergedCell(sheet, firstRow, lastRow, firstCol, lastCol, mergedHeader);
            createHeaderRow(sheet, lastRow + 1, firstCol, headerMap.get(keys[i]));


            List<Map<String, Object>> dataList = dataMap.get(keys[i]);
            writeData(workbook, sheet, lastRow + 1 , firstCol, dataList, outputStream);
            firstCol = firstCol + headerList.size();
        }
    }

    private void write2Workbook(Workbook workbook, Sheet sheet,
                                int firstRow, int firstCol,
                                                     List<String> headerList,
                                                    List<Map<String, Object>> dataList,
                                                    OutputStream outputStream) {

        createHeaderRow(sheet, firstRow, firstCol, headerList);
        writeData(workbook, sheet, firstRow, firstCol, dataList, outputStream);
    }

    /**
     * 设置合并单元格边框 - 线条
     */
    private void setBorderStyle(Sheet sheet, CellRangeAddress region) {
        // 合并单元格左边框样式
        RegionUtil.setBorderLeft(BorderStyle.THICK, region, sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.LIGHT_BLUE.getIndex(), region, sheet);

        // 合并单元格上边框样式
        RegionUtil.setBorderTop(BorderStyle.THICK, region, sheet);
        RegionUtil.setTopBorderColor(IndexedColors.LIGHT_ORANGE.getIndex(), region, sheet);

        // 合并单元格右边框样式
        RegionUtil.setBorderRight(BorderStyle.THICK, region, sheet);
        RegionUtil.setRightBorderColor(IndexedColors.LIGHT_BLUE.getIndex(), region, sheet);

        // 合并单元格下边框样式
        RegionUtil.setBorderBottom(BorderStyle.THICK, region, sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.LIGHT_ORANGE.getIndex(), region, sheet);
    }

    private Workbook getWorkbook(String excelFilePath) {
        Workbook workbook;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    public List<Book> getListBook() {
        Book book1 = new Book();
        book1.setTitle("Head First Java");
        book1.setAuthor("Kathy Serria");
        book1.setPrice(79);
        Book book2 = new Book();
        book2.setTitle("Effective Java");
        book2.setAuthor("Joshua Bloch");
        book2.setPrice(36);

        List<Book> listBook = Arrays.asList(book1, book2);

        return listBook;
    }

    public List<People> getListPeople() {
        People people = new People();
        people.setName("lily");
        people.setAge(20);
        people.setSex("女");
        people.setScore(88.3);

        People people1 = new People();
        people1.setName("Tom");
        people1.setAge(21);
        people1.setSex("男");
        people1.setScore(78.3);


        List<People> list= Arrays.asList(people, people1);

        return list;
    }

    public static Map<String, Object> toMap(Object object) {
        ObjectMapper oMapper = new ObjectMapper();
        return oMapper.convertValue(object, Map.class);
    }

    public static void main(String[] args) {
        ExcelWriter excelWriter = new ExcelWriter();

        List<Book> listBook = excelWriter.getListBook();
        List<Map<String, Object>> data = listBook.stream().map(ExcelWriter::toMap).collect(Collectors.toList());

        List<People> listPeople = excelWriter.getListPeople();
        List<Map<String, Object>> dataPeople = listPeople.stream().map(ExcelWriter::toMap).collect(Collectors.toList());

        List<String> headerList = Arrays.asList("title", "author", "price");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("上海", Arrays.asList("宝山区", "闵行区", "静安区"));
        headerMap.put("北京", Arrays.asList("朝阳区", "东城区", "西城区", "海淀区"));

        Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();
        dataMap.put("上海",data);
        dataMap.put("北京",dataPeople);

        OutputStream outputStream = null;


        excelWriter.write2WorkbookWithMergedCellHeader(workbook, sheet, 0, 0, 0, headerMap, dataMap, outputStream);
//        excelWriter.write2Workbook(workbook,sheet,0,0,headerList, data, outputStream);

        try {
            String filename = "JavaBoosk1.xlsx";
            outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            outputStream.flush();

        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }

    }
}
