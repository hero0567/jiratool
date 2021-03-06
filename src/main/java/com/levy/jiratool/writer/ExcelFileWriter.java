package com.levy.jiratool.writer;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class ExcelFileWriter implements FileWriter {
    @Override
    public void write(List<String> contents) {
        createExcel(contents, "issueresult.xlsx");
    }

    private static void createExcel(List<String> contents, String path) {
        log.info("Will save data into: {}", path);
        try {
            XSSFWorkbook wb = new XSSFWorkbook();  //创建工作薄
            XSSFSheet sheet = wb.createSheet("RootCause"); //创建工作表
            for (int i = 0; i < contents.size(); i++) {
                String rowData = contents.get(i);
                XSSFRow row = sheet.createRow(i); //行
                String[] rowArry = rowData.split(";");
                for (int j = 0; j < rowArry.length; j++) {
                    row.createCell(j).setCellValue(rowArry[j]);
                }
            }

            String[] headers = contents.get(0).split(";");
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream outputStream = new FileOutputStream(path);) {
                wb.write(outputStream);
                outputStream.flush();
            }
            log.info("Success save data to file.");
        } catch (Exception e) {
            log.error("Faild save data to file.", e);
        }
    }

    private void cellStyle(XSSFWorkbook wb) {
        CellStyle cellStyle = wb.createCellStyle(); // 创建单元格样式
    }

}
