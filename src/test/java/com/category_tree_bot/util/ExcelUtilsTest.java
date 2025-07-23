package com.category_tree_bot.util;

import com.category_tree_bot.entity.Category;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelUtilsTest {

    @Test
    void testParseExcelFile() throws Exception {
        // Создание временного Excel-файла
        File tempFile = File.createTempFile("test-categories", ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Categories");

            // Заголовки
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Code");
            header.createCell(2).setCellValue("ParentCode");

            // Строка 1
            var row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Категория A");
            row1.createCell(1).setCellValue("A");
            row1.createCell(2).setCellValue("");

            // Строка 2
            var row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Категория B");
            row2.createCell(1).setCellValue("B");
            row2.createCell(2).setCellValue("A");

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                workbook.write(out);
            }
        }

        // Тестируем парсинг
        List<Category> result = ExcelUtils.parseExcelFile(tempFile);

        assertEquals(2, result.size());

        Category first = result.get(0);
        assertEquals("Категория A", first.getName());
        assertEquals("A", first.getCode());
        assertEquals("", first.getParentCode());

        Category second = result.get(1);
        assertEquals("Категория B", second.getName());
        assertEquals("B", second.getCode());
        assertEquals("A", second.getParentCode());

        // Удалить временный файл
        tempFile.delete();
    }
}
