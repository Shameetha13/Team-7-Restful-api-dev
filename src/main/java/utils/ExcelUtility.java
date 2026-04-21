package utils;

import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.*;

public class ExcelUtility {

    public String getDataFromExcel(String sheetName, int rowNum, int cellNum) throws Exception {

        FileInputStream fis = new FileInputStream("./testdata/testscriptdata.xlsx");
        Workbook wb = WorkbookFactory.create(fis);

        Sheet sheet = wb.getSheet(sheetName);
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(cellNum);

        DataFormatter formatter = new DataFormatter();
        String data = formatter.formatCellValue(cell);

        wb.close();
        fis.close();

        return data;
    }
}