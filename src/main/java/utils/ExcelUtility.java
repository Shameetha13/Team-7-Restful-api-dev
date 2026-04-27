package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtility {

    private static final String FILE_PATH = "src/test/resources/testdata/Group 7 DDT.xlsx";

    public static String getCellData(String sheetName, int rowNum, int colNum) {
        String cellValue = "";
        try {
            FileInputStream fis = new FileInputStream(FILE_PATH);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file.");
            }

            Row row = sheet.getRow(rowNum + 1); // +1 to skip header row
            if (row == null) {
                throw new RuntimeException("Row " + rowNum + " not found in sheet '" + sheetName + "'.");
            }

            Cell cell = row.getCell(colNum);
            if (cell == null) {
                return "";
            }

            DataFormatter formatter = new DataFormatter();
            cellValue = formatter.formatCellValue(cell);

            workbook.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }

        return cellValue;
    }

    public static int getRowCount(String sheetName, int colNum) {
        int rowCount = 0;
        try {
            FileInputStream fis = new FileInputStream(FILE_PATH);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file.");
            }

            // Start from row 1 to skip header
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) break;

                Cell cell = row.getCell(colNum);
                if (cell == null) break;

                DataFormatter formatter = new DataFormatter();
                String value = formatter.formatCellValue(cell).trim();

                if (value.isEmpty()) break; // stop counting when cell is empty
                rowCount++;
            }

            workbook.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }

        return rowCount;
        
    }
    public static Map<String, String> getRowDataAsMap(String sheetName, int rowNum) {
        Map<String, String> rowMap = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(FILE_PATH);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);
            
            Row headerRow = sheet.getRow(0);
            Row dataRow = sheet.getRow(rowNum + 1);

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = headerRow.getCell(i).getStringCellValue();
                String value = new DataFormatter().formatCellValue(dataRow.getCell(i));
                rowMap.put(header, value);
            }
            workbook.close();
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel row: " + e.getMessage());
        }
        return rowMap;
    }
}