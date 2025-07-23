package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DownloadCommandHandlerTest {

    private CategoryRepository repository;
    private DownloadCommandHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        handler = new DownloadCommandHandler(repository);
    }

    @Test
    void testGetCommand() {
        assertEquals("/download", handler.getCommand());
    }

    @Test
    void testHandleAlwaysReturnsNull() {
        assertNull(handler.handle(new String[]{"any"}, 123L));
    }

    @Test
    void testGenerateExcelFile() throws Exception {
        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Parent");

        Category child = new Category();
        child.setId(2L);
        child.setName("Child");
        child.setParent(parent);

        Category root = new Category();
        root.setId(3L);
        root.setName("Root");
        root.setParent(null);

        when(repository.findAll()).thenReturn(List.of(child, root));

        byte[] excelBytes = handler.generateExcelFile();

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Header
            Row header = sheet.getRow(0);
            assertEquals("ID", header.getCell(0).getStringCellValue());
            assertEquals("Название", header.getCell(1).getStringCellValue());
            assertEquals("Родитель", header.getCell(2).getStringCellValue());

            // Row 1: Child
            Row row1 = sheet.getRow(1);
            assertEquals(2L, (long) row1.getCell(0).getNumericCellValue());
            assertEquals("Child", row1.getCell(1).getStringCellValue());
            assertEquals("Parent", row1.getCell(2).getStringCellValue());

            // Row 2: Root
            Row row2 = sheet.getRow(2);
            assertEquals(3L, (long) row2.getCell(0).getNumericCellValue());
            assertEquals("Root", row2.getCell(1).getStringCellValue());
            assertEquals("—", row2.getCell(2).getStringCellValue());
        }
    }
}
