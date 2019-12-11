package org.rabbit.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.WorkBookUtil;
import com.alibaba.excel.write.merge.LoopMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alibaba.excel.support.ExcelTypeEnum.XLSX;

/**
 * easyexcel 快速应该指使用了流式接口，官网的例子只有25列
 * easyexcel创建合并列的底层api也是sheet.addMergedRegionUnsafe(region)
 * easyexcel支持的数据schema为class和list，不灵活，不支持分组的Map结构
 */
public class EasyExcelWriter {


    public static void main(String[] args) {

        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setExcelType(XLSX);
        writeWorkbook.setInMemory(false);
        WriteWorkbookHolder workbookHolder = new WriteWorkbookHolder(writeWorkbook);
        try {
            WorkBookUtil.createWorkBook(workbookHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Workbook workbook = workbookHolder.getWorkbook();

        String filename = "output1.xlsx";

        EasyExcel.writerSheet("sheet1");


        List<Object> people1 = new ArrayList<Object>() {{
            add("lily");
            add("feman");
            add(18);
            add(98.2);
        }};

        List<Object> people2 = new ArrayList<Object>() {{
            add("tom");
            add("man");
            add(19);
            add(88.2);
        }};

        List<List<Object>> data = new ArrayList<>();
        data.add(people1);
        data.add(people2);
//        EasyExcel.write(filename).sheet("模板").doWrite(ModuleHelper.getListPeople());

//        LoopMergeStrategy loopMergeStrategy = new LoopMergeStrategy(1,10, 0);
//        EasyExcel.write(filename)
//                .registerWriteHandler(loopMergeStrategy)
//                .sheet("模板").doWrite(demoData());

//        String fileName = "file.xlsx";
//        EasyExcel.write(fileName, DemoData.class)
//                .sheet("模板")
//                .registerWriteHandler(new LoopMergeStrategy(1, 2, 1))
//                .automaticMergeHead(true)
//                .doWrite(demoData());


        try{
            OutputStream outputStream = new FileOutputStream(filename);
            ExcelWriter writer = EasyExcelFactory.getWriter(outputStream);
            WriteSheet sheet = new WriteSheet();
            sheet.setSheetName("sheet1");
            writer.write(demoData(),sheet);

        }catch (Exception e){

        }
    }

    public static List<DemoData> demoData() {
        return Stream.iterate(1, i -> i + 1)
                .limit(200)
                .map(integer -> {
                    final DemoData demoData = new DemoData();
                    demoData.setIndex(integer);
                    demoData.setString("name " + integer);
                    demoData.setDate(new Date());
                    demoData.setDoubleData(0.1);
                    return demoData;
                }).collect(Collectors.toList());
    }

    @Data
    static class DemoData{
        int index;
        String string;
        Date date;
        Double doubleData;

    }

}
