package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtility {

    private static final String FILE_PATH =
            System.getProperty("user.dir") + "/src/test/resources/testdata/testdata.xlsx";

    private static final String SHEET_NAME = "Sheet1";

    // ✅ Read data row-wise and return as Map
    public static Map<String, String> getRowData(int rowNum) {

        Map<String, String> data = new HashMap<>();

        try {
            FileInputStream fis = new FileInputStream(FILE_PATH);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheet(SHEET_NAME);

            Row headerRow = sheet.getRow(0); // first row = column names
            Row row = sheet.getRow(rowNum);

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {

                String key = headerRow.getCell(i).getStringCellValue();
                Cell cell = row.getCell(i);

                String value = "";

                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            value = cell.getStringCellValue();
                            break;

                        case NUMERIC:
                            value = String.valueOf((int) cell.getNumericCellValue());
                            break;

                        case BOOLEAN:
                            value = String.valueOf(cell.getBooleanCellValue());
                            break;

                        default:
                            value = "";
                    }
                }

                data.put(key, value);
            }

            workbook.close();
            fis.close();

        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file", e);
        }

        return data;
    }
}