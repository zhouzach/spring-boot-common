package org.rabbit.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlowExcelWriter {


    public static void main(String[] args) {
        SlowExcelWriter slowExcelWriter = new SlowExcelWriter();

        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        SXSSFSheet sxssfSheet = sxssfWorkbook.createSheet("sheet1");
        sxssfSheet.setRandomAccessWindowSize(-1);

        String rowName = "Date";
        List<Optional<String>> measureNames = Arrays.asList(Optional.of("Sales"), Optional.of("GC"), Optional.of("AC"));
        int dataUnitLength = 5;
        Object[] data1= {"2019-11-19", "city1", "10000.23", 223, "39.54"};
        Object[] data2= {"2019-11-20", "city1", "11000.23", 233, "69.54"};
        Object[] data3= {"2019-11-19", "city2", "41000.23", 833, "49.54"};
        Object[] data4= {"2019-11-20", "city2", "21000.23", 433, "89.54"};
        //it is very slow when there is more than 3000 cities;

        Map<Object, List<Object[]>> sortCityMap = new HashMap<>();
        sortCityMap.put("city1",Arrays.asList(data1, data2));
        sortCityMap.put("city2",Arrays.asList(data3, data4));

        Map<Object, List<Object[]>> sortDateMap = new HashMap<>();
        sortDateMap.put("2019-11-19",Arrays.asList(data1, data3));
        sortDateMap.put("2019-11-20",Arrays.asList(data2, data4));

        slowExcelWriter.writeMap2Sheet4RowColMeasure(sxssfSheet, rowName, 0, 1, 0, sortCityMap, measureNames, sortDateMap, dataUnitLength);;

        try {
            OutputStream outputStream = new FileOutputStream("output3000cities.xlsx");
            sxssfWorkbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeMap2Sheet4RowColMeasure(Sheet sheet,
                                              String rowName,
                                              int firstRow,
                                              int lastRow,
                                              int firstCol,
                                              Map<Object, List<Object[]>> sortCityMap,
                                              List<Optional<String>> headerUnitList,
                                              Map<Object, List<Object[]>> sortDateMap,
                                              int arrayLength) {

        int lastCol = firstCol;
        int mergedRowColLastCol = firstCol;
        createMergedCell(sheet, firstRow, lastRow, firstCol, mergedRowColLastCol, rowName);

        List<Object> rowData = new ArrayList<>(sortDateMap.keySet());
        writeRowHeaderData(sheet, lastRow + 1, firstCol, rowData);

        int headerUnitSize = headerUnitList.size();

        Object[] mergedHeaders = sortCityMap.keySet().toArray();

        int mergedCellFirstCol = mergedRowColLastCol + 1;
        int mergedCellLastCol = mergedRowColLastCol + headerUnitSize;

        int measureHeaderFirstCol = mergedRowColLastCol + 1;

        int dataFirstCol = mergedRowColLastCol + 1;

        for (int i = 0; i < mergedHeaders.length; i++) {

            lastCol = lastCol + headerUnitSize;

            createMergedCell(sheet, firstRow, lastRow - 1, mergedCellFirstCol, mergedCellLastCol, String.valueOf(mergedHeaders[i]));
            mergedCellFirstCol = mergedCellLastCol + 1;
            mergedCellLastCol = mergedCellLastCol + headerUnitSize;

            createMeasureHeader(sheet, lastRow, measureHeaderFirstCol, headerUnitList);
            measureHeaderFirstCol = measureHeaderFirstCol + headerUnitSize;

            List<Object[]> colData = sortCityMap.get(mergedHeaders[i]);
            writeArrayData(sheet, lastRow + 1, dataFirstCol, colData, rowData, arrayLength);
            dataFirstCol = dataFirstCol + headerUnitSize;

            if (0 == i % 500) {
                System.out.println("finish " + i + " col data: " + System.currentTimeMillis());
            }
        }


    }

    private void createMergedCell(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, String value) {

        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegionUnsafe(region);

        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);

        CellStyle cellStyle = setMergedHeaderStyle(sheet);
        cell.setCellStyle(cellStyle);


        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);

        setBorderStyle4Region(sheet, region);
    }

    private void setBorderStyle4Region(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);

        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);

        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);

        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
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


    private void writeRowHeaderData(Sheet sheet, Integer x, Integer y, List<Object> data) {
        for (int i = x, j = 0; j < data.size(); i++, j++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            setRowHeaderValue(y, data.get(j), row, sheet);
        }
    }
    private void setRowHeaderValue(Integer y, Object data, Row row, Sheet sheet) {
        Cell cell;
        cell = row.createCell(y);
        cell.setCellValue(String.valueOf(data));
    }

    private void createMeasureHeader(Sheet sheet, int x, int y, List<Optional<String>> measures) {

        Row row = sheet.getRow(x);
        if (row == null) {
            row = sheet.createRow(x);
        }
        CellStyle cellStyle = setHeaderStyle(sheet);

        Cell cell;
        for (int k = 0; k < measures.size(); y++, k++) {
            cell = row.getCell(y);
            if (cell == null) {
                cell = row.createCell(y);
            }
            cell.setCellStyle(cellStyle);
            cell.setCellValue(measures.get(k).get());
        }
    }

    private CellStyle setHeaderStyle(Sheet sheet) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();

        Font font = setHeaderFont(sheet);
        cellStyle.setFont(font);

        return setBorderStyle(cellStyle);
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

    private Font setHeaderFont(Sheet sheet) {
        Font font = sheet.getWorkbook().createFont();
        font.setBold(false);
        font.setFontHeightInPoints((short) 14);
        return font;
    }


    private void writeArrayData(Sheet sheet, Integer x, Integer y, List<Object[]> keyList, List<Object> rowData, int arrayLength) {

        for (int i = x, j = 0; j < keyList.size(); i++, j++) {

            Object[] keyData = keyList.get(j);
            Object rowValue = keyList.get(j)[0];

            int n = rowData.indexOf(rowValue);
            Row row = sheet.getRow(n + x);
            if (row == null) {
                row = sheet.createRow(n + x);
            }

            setCellValueByArrayRecord(y, keyData, 2, arrayLength, row, sheet); //row and col are single,so offset is 2
        }
    }

    private void setCellValueByArrayRecord(Integer y, Object[] dataArray, int dataOffset, int arrayLength, Row row, Sheet sheet) {
        Cell cell;
        for (int k = dataOffset; k < arrayLength; k++, y++) {
            cell = row.getCell(y);
            if (cell == null) {
                cell = row.createCell(y);
            }
            setCellFixedValue(cell, dataArray[k]);
        }
    }

    private void setCellFixedValue(Cell cell, Object object) {
        int precision = 1000;
        if (null == object) {
            cell.setCellValue("NaN");
            return;
        }

        String value = String.valueOf(object);

        if (isNumeric(value)) {
            DecimalFormat df = new DecimalFormat("#");
            DecimalFormat df1 = new DecimalFormat("#.#");

            long longValue;
            double fixedDoubleValue;
            int i = value.indexOf(".");
            if (-1 != i) {
                double doubleValue = Double.parseDouble(value);

                if (doubleValue > precision) {
                    df.setRoundingMode(RoundingMode.DOWN);
                    longValue = Long.parseLong(df.format(doubleValue));
                    cell.setCellValue(longValue);
                } else {
                    fixedDoubleValue = Double.parseDouble(df1.format(doubleValue));
                    cell.setCellValue(fixedDoubleValue);
                }
            } else {
                cell.setCellValue(Long.parseLong(value));
            }
        } else {
            cell.setCellValue(value);
        }

    }


    public static boolean isNumeric(final String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }

        Matcher isNum = pattern.matcher(bigStr);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
