package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddElementCommandHandlerTest {

    private CategoryRepository repository;
    private AddElementCommandHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        handler = new AddElementCommandHandler(repository);
    }

    @Test
    void testCommandName() {
        assertEquals("/addElement", handler.getCommand());
    }

    @Test
    void testHandle_noArgs() {
        String[] args = {};
        String result = handler.handle(args, 1L);
        assertTrue(result.contains("❌ Ошибка"));
    }

    @Test
    void testHandle_rootCategory_alreadyExists() {
        String[] args = {"Books"};
        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.of(new Category()));
        String result = handler.handle(args, 1L);
        assertTrue(result.contains("⚠️ Такая корневая категория уже существует."));
    }

    @Test
    void testHandle_rootCategory_createdSuccessfully() {
        String[] args = {"Books"};
        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.empty());
        String result = handler.handle(args, 1L);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(captor.capture());

        Category saved = captor.getValue();
        assertEquals("Books", saved.getName());
        assertNull(saved.getParent());

        assertTrue(result.contains("✅ Корневая категория 'Books' успешно добавлена."));
    }

    @Test
    void testHandle_subCategory_parentNotFound() {
        String[] args = {"Books", "Fiction"};
        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.empty());
        String result = handler.handle(args, 1L);
        assertTrue(result.contains("❌ Родительская категория 'Books' не найдена."));
    }

    @Test
    void testHandle_subCategory_alreadyExists() {
        String[] args = {"Books", "Fiction"};
        Category parent = new Category();
        parent.setName("Books");

        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.of(parent));
        when(repository.findByNameAndParent("Fiction", parent)).thenReturn(Optional.of(new Category()));

        String result = handler.handle(args, 1L);
        assertTrue(result.contains("⚠️ Подкатегория 'Fiction' уже существует"));
    }

    @Test
    void testHandle_subCategory_createdSuccessfully() {
        String[] args = {"Books", "Fiction"};
        Category parent = new Category();
        parent.setName("Books");

        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.of(parent));
        when(repository.findByNameAndParent("Fiction", parent)).thenReturn(Optional.empty());

        String result = handler.handle(args, 1L);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(captor.capture());

        Category saved = captor.getValue();
        assertEquals("Fiction", saved.getName());
        assertEquals(parent, saved.getParent());

        assertTrue(result.contains("✅ Подкатегория 'Fiction' добавлена в 'Books'"));
    }

    @Test
    void testHandle_invalidFormat_moreThanTwoArgs() {
        String[] args = {"A", "B", "C"};
        String result = handler.handle(args, 1L);
        assertTrue(result.contains("❌ Неверный формат команды"));
    }
}
