package org.rabbit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
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
import java.util.*;

public class ExcelWriter {


    private void writeData(Sheet sheet, Integer x, Integer y, List<Map<String, Object>> data) {

        int n = x;
        for (Map<String, Object> map : data) {
            Row row = sheet.getRow(n);
            if (row == null) {
                row = sheet.createRow(n);
            }
            setCellValue(y, map, row, sheet);
            n++;
        }

    }

    private void setCellValue(Integer y, Map<String, Object> map, Row row, Sheet sheet) {
        Object[] keys = map.keySet().toArray();
        Cell cell;
        for (int k = 0; k < keys.length; k++, y++) {
            cell = row.createCell(y);
            cell.setCellValue(String.valueOf(map.get(keys[k])));

            CellStyle cellStyle = setBorderStyle(sheet.getWorkbook().createCellStyle());
            cell.setCellStyle(cellStyle);
        }
    }

    private CellStyle setMergedHeaderStyle(Sheet sheet) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();

        Font font = setMergedHeaderFont(sheet);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private Font setMergedHeaderFont(Sheet sheet) {
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        return font;
    }

    private Font setHeaderFont(Sheet sheet) {
        Font font = sheet.getWorkbook().createFont();
        font.setBold(false);
        font.setFontHeightInPoints((short) 14);
        return font;
    }

    private CellStyle setHeaderStyle(Sheet sheet) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();

        Font font = setHeaderFont(sheet);
        cellStyle.setFont(font);

        return setBorderStyle(cellStyle);
    }

    private void createHeaderRow(Sheet sheet, int x, int y, List<String> headerList) {

        Row row = sheet.getRow(x);
        if (row == null) {
            row = sheet.createRow(x);
        }
        CellStyle cellStyle = setHeaderStyle(sheet);

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
        Row row = sheet.getRow(firstRow); //判断是在同一行追加还是新建一行
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);

        CellStyle cellStyle = setMergedHeaderStyle(sheet);
        cell.setCellStyle(cellStyle);

        // 设置单元格内容水平垂直居中
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);

        // 3.设置合并单元格边框
        setBorderStyle4Region(sheet, region);
    }

    public void write2SheetWithMergedCellHeader(Sheet sheet,
                                                 Map<String, List<String>> headerMap,
                                                 Map<String, List<Map<String, Object>>> dataMap) {
        write2SheetWithMergedCellHeader(sheet, 0, 0, 0, headerMap, dataMap);
    }

    private void write2SheetWithMergedCellHeader(Sheet sheet, int firstRow, int lastRow, int firstCol,
                                                 Map<String, List<String>> headerMap,
                                                 Map<String, List<Map<String, Object>>> dataMap
    ) {
        int lastCol = 0;
        val keys = headerMap.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String mergedHeader = String.valueOf(keys[i]);
            List<String> headerList = headerMap.get(keys[i]);

            lastCol = firstCol + headerList.size() - 1;
            createMergedCell(sheet, firstRow, lastRow, firstCol, lastCol, mergedHeader);
            createHeaderRow(sheet, lastRow + 1, firstCol, headerMap.get(keys[i]));


            List<Map<String, Object>> dataList = dataMap.get(keys[i]);
            writeData(sheet, lastRow + 1 + 1, firstCol, dataList);
            firstCol = firstCol + headerList.size();
        }
    }

    public void write2Sheet(Sheet sheet,
                             List<String> headerList,
                             List<Map<String, Object>> dataList) {
        write2Sheet(sheet, 0, 0, headerList, dataList);

    }

    private void write2Sheet(Sheet sheet,
                             int firstRow, int firstCol,
                             List<String> headerList,
                             List<Map<String, Object>> dataList) {

        createHeaderRow(sheet, firstRow, firstCol, headerList);
        writeData(sheet, firstRow + 1, firstCol, dataList);
    }

    public void write2File(Workbook workbook, String filename) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filename);
            write2OutputStream(workbook,outputStream);
        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void write2OutputStream(Workbook workbook, OutputStream outputStream) {
        try {
            workbook.write(outputStream);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    /**
     * 设置合并单元格边框 - 线条
     */
    private void setBorderStyle4Region(Sheet sheet, CellRangeAddress region) {
        // 合并单元格左边框样式
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
//        RegionUtil.setLeftBorderColor(IndexedColors.LIGHT_BLUE.getIndex(), region, sheet);

        // 合并单元格上边框样式
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
//        RegionUtil.setTopBorderColor(IndexedColors.LIGHT_ORANGE.getIndex(), region, sheet);

        // 合并单元格右边框样式
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
//        RegionUtil.setRightBorderColor(IndexedColors.LIGHT_BLUE.getIndex(), region, sheet);

        // 合并单元格下边框样式
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
//        RegionUtil.setBottomBorderColor(IndexedColors.LIGHT_ORANGE.getIndex(), region, sheet);
    }

    private CellStyle setBorderStyle(CellStyle cellStyle) {
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);

        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }

    public Sheet createSheetByName(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    public Sheet createSheetByName(String sheetName) {
        return getWorkbookByFilename("*.xls").createSheet(sheetName);
    }

    public Sheet createSheetByName(String excelFilePath, String sheetName) {
        return getWorkbookByFilename(excelFilePath).createSheet(sheetName);
    }

    public Workbook getWorkbookByFilename(String excelFilePath) {
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



    public static Map<String, Object> toMap(Object object) {
        ObjectMapper oMapper = new ObjectMapper();
        return oMapper.convertValue(object, Map.class);
    }

    public static void main(String[] args) {
        ExcelWriter excelWriter = new ExcelWriter();

//        List<Book> listBook = excelWriter.getListBook();
//        List<Map<String, Object>> data = listBook.stream().map(ExcelWriter::toMap).collect(Collectors.toList());
        List<Map<String, Object>> data1 = new ArrayList<>();

        data1.add(ImmutableMap.<String, Object>builder()
                .put("k1", "v1")
                .put("k2", "v2")
                .put("k3", "v3")
                .build());
        data1.add(ImmutableMap.<String, Object>builder()
                .put("m1", "n1")
                .put("m2", "n2")
                .put("m3", "n3")
                .build());

//        List<People> listPeople = excelWriter.getListPeople();
//        List<Map<String, Object>> dataPeople = listPeople.stream().map(ExcelWriter::toMap).collect(Collectors.toList());
        List<Map<String, Object>> data2 = new ArrayList<>();

        data2.add(ImmutableMap.<String, Object>builder()
                .put("k1", "v1")
                .put("k2", "v2")
                .put("k3", "v3")
                .put("k4", "v4")
                .build());
        data2.add(ImmutableMap.<String, Object>builder()
                .put("m1", "n1")
                .put("m2", "n2")
                .put("m3", "n3")
                .put("m4", "n4")
                .build());

        List<String> headerList = Arrays.asList("title", "author", "price");

        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("上海", Arrays.asList("宝山区", "闵行区", "静安区"));
        headerMap.put("北京", Arrays.asList("朝阳区", "东城区", "西城区", "海淀区"));

        Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();
        dataMap.put("上海", data1);
        dataMap.put("北京", data2);


        String filename = "output.xls";
//        String filename = "output.xlsx";
        Workbook workbook = excelWriter.getWorkbookByFilename(filename);
        Sheet sheet1 = excelWriter.createSheetByName(workbook, "sheet1");
        Sheet sheet2 = excelWriter.createSheetByName(workbook, "sheet2");

        excelWriter.write2Sheet(sheet1, headerList, data1);
        excelWriter.write2SheetWithMergedCellHeader(sheet2, headerMap, dataMap);
//        excelWriter.write2Sheet(sheet1, 3, 3, headerList, data1);
//        excelWriter.write2SheetWithMergedCellHeader(sheet2, 4, 5, 4, headerMap, dataMap);

        excelWriter.write2File(workbook, filename);

    }
}
