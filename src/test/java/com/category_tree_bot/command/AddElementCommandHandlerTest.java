package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testGetCommand() {
        assertEquals("/addElement", handler.getCommand());
    }

    @Test
    void testHandleEmptyArgs() {
        String result = handler.handle(new String[]{}, 1L);
        assertEquals("❌ Ошибка: укажите хотя бы одно имя категории.", result);
    }

    @Test
    void testAddRootCategory_Success() {
        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.empty());

        String result = handler.handle(new String[]{"\"Books\""}, 1L);

        assertEquals("✅ Корневая категория 'Books' успешно добавлена.", result);
        verify(repository).save(any(Category.class));
    }

    @Test
    void testAddRootCategory_AlreadyExists() {
        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.of(new Category()));

        String result = handler.handle(new String[]{"\"Books\""}, 1L);

        assertEquals("⚠️ Такая корневая категория уже существует.", result);
        verify(repository, never()).save(any());
    }

    @Test
    void testAddSubCategory_Success() {
        Category parent = new Category();
        parent.setName("Books");

        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.of(parent));
        when(repository.findByNameAndParent("Fiction", parent)).thenReturn(Optional.empty());

        String result = handler.handle(new String[]{"\"Books\"", "\"Fiction\""}, 1L);

        assertEquals("✅ Подкатегория 'Fiction' добавлена в 'Books'.", result);
        verify(repository).save(any(Category.class));
    }

    @Test
    void testAddSubCategory_ParentNotFound() {
        when(repository.findByNameAndParentIsNull("Nonexistent")).thenReturn(Optional.empty());

        String result = handler.handle(new String[]{"\"Nonexistent\"", "\"Child\""}, 1L);

        assertEquals("❌ Родительская категория 'Nonexistent' не найдена.", result);
        verify(repository, never()).save(any());
    }

    @Test
    void testAddSubCategory_AlreadyExists() {
        Category parent = new Category();
        parent.setName("Books");

        when(repository.findByNameAndParentIsNull("Books")).thenReturn(Optional.of(parent));
        when(repository.findByNameAndParent("Fiction", parent)).thenReturn(Optional.of(new Category()));

        String result = handler.handle(new String[]{"\"Books\"", "\"Fiction\""}, 1L);

        assertEquals("⚠️ Подкатегория 'Fiction' уже существует в 'Books'.", result);
        verify(repository, never()).save(any());
    }

    @Test
    void testHandle_InvalidFormat() {
        String result = handler.handle(new String[]{"\"A\"", "\"B\"", "\"C\""}, 1L);
        assertEquals("❌ Неверный формат команды. Используйте:\n" +
                "/addElement \"Название\"\n" +
                "или\n" +
                "/addElement \"Родитель\" \"Подкатегория\"", result);
    }

    @Test
    void testParseArgs_QuotesAndWordsMixed() {
        List<String> parsed = handler.handle(new String[]{"\"A", "B\""}, 1L).contains("успешно") ?
                List.of("A B") : List.of();
        assertEquals(List.of("A B"), parsed);
    }
}
