package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import com.category_tree_bot.service.CategoryDeletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoveElementCommandHandlerTest {

    private CategoryRepository repository;
    private CategoryDeletionService deletionService;
    private RemoveElementCommandHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        deletionService = mock(CategoryDeletionService.class);
        handler = new RemoveElementCommandHandler(repository, deletionService);
    }

    @Test
    void testGetCommand() {
        assertEquals("/removeElement", handler.getCommand());
    }

    @Test
    void testHandle_NoArgs() {
        String result = handler.handle(new String[]{}, 123L);
        assertTrue(result.contains("Ошибка"));
    }

    @Test
    void testHandle_CategoryNotFound() {
        when(repository.findByNameWithChildren("Test")).thenReturn(Optional.empty());

        String result = handler.handle(new String[]{"Test"}, 123L);

        assertTrue(result.contains("не найдена"));
        verifyNoInteractions(deletionService);
    }

    @Test
    void testHandle_CategoryFoundAndDeleted() {
        Category mockCategory = new Category();
        mockCategory.setName("Test");

        when(repository.findByNameWithChildren("Test")).thenReturn(Optional.of(mockCategory));

        String result = handler.handle(new String[]{"Test"}, 123L);

        verify(deletionService).deleteRecursively(mockCategory);
        assertTrue(result.contains("удалены"));
    }
}
