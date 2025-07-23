package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import com.category_tree_bot.util.ExcelUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadCommandHandlerTest {

    private CategoryRepository repository;
    private UploadCommandHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        handler = new UploadCommandHandler(repository);
    }

    @Test
    void testGetCommand() {
        assertEquals("/upload", handler.getCommand());
    }

    @Test
    void testHandleWithoutFile() {
        String result = handler.handle(new String[]{"arg1"}, 123L);
        assertEquals("", result);
    }

    @Test
    void testHandleWithFileSuccess() throws Exception {
        File mockFile = mock(File.class);
        List<Category> mockCategories = List.of(new Category());

        try (MockedStatic<ExcelUtils> utilities = mockStatic(ExcelUtils.class)) {
            utilities.when(() -> ExcelUtils.parseExcelFile(mockFile)).thenReturn(mockCategories);

            String result = handler.handle(new String[]{}, 123L, mockFile);

            assertEquals("✅ Данные из Excel успешно загружены.", result);
            verify(repository).saveAll(mockCategories);
        }
    }

    @Test
    void testHandleWithFileFailure() throws Exception {
        File mockFile = mock(File.class);

        try (MockedStatic<ExcelUtils> utilities = mockStatic(ExcelUtils.class)) {
            utilities.when(() -> ExcelUtils.parseExcelFile(mockFile)).thenThrow(new RuntimeException("Test error"));

            String result = handler.handle(new String[]{}, 123L, mockFile);

            assertTrue(result.contains("❌ Ошибка при загрузке Excel"));
            verify(repository, never()).saveAll(any());
        }
    }

}
