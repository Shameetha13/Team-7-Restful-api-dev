package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtility {

    private static final String EXCEL_PATH = System.getProperty("user.dir") 
                                             + "/src/test/resources/testdata/TestData.xlsx";

    private static Workbook getWorkbook(String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(EXCEL_PATH);
        return new XSSFWorkbook(fis);
    }

    public static String getCellData(String sheetName, int rowIndex, int colIndex) {
        try {
            Workbook workbook = getWorkbook(sheetName);
            Sheet sheet       = workbook.getSheet(sheetName);
            Row row           = sheet.getRow(rowIndex);
            Cell cell         = row.getCell(colIndex);

            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell).trim();

        } catch (Exception e) {
            System.out.println("ExcelUtils Error - getCellData: " + e.getMessage());
            return "";
        }
    }

    public static int getColumnIndex(String sheetName, String columnName) {
        try {
            Workbook workbook = getWorkbook(sheetName);
            Sheet sheet       = workbook.getSheet(sheetName);
            Row headerRow     = sheet.getRow(0);

            for (Cell cell : headerRow) {
                if (cell.getStringCellValue().trim().equalsIgnoreCase(columnName)) {
                    return cell.getColumnIndex();
                }
            }
        } catch (Exception e) {
            System.out.println("ExcelUtils Error - getColumnIndex: " + e.getMessage());
        }
        return -1;
    }

    public static int getRowIndex(String sheetName, String collectionName) {
        try {
            Workbook workbook = getWorkbook(sheetName);
            Sheet sheet       = workbook.getSheet(sheetName);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                Cell cell = row.getCell(0);
                if (cell != null && cell.getStringCellValue().trim()
                        .equalsIgnoreCase(collectionName)) {
                    return row.getRowNum();
                }
            }
        } catch (Exception e) {
            System.out.println("ExcelUtils Error - getRowIndex: " + e.getMessage());
        }
        return -1;
    }

    public static int getRowCount(String sheetName) {
        try {
            Workbook workbook = getWorkbook(sheetName);
            Sheet sheet       = workbook.getSheet(sheetName);
            return sheet.getLastRowNum(); // Row 0 is header
        } catch (Exception e) {
            System.out.println("ExcelUtils Error - getRowCount: " + e.getMessage());
            return 0;
        }
    }
}