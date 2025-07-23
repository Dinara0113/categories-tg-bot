package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewTreeCommandHandlerTest {

    private CategoryRepository repository;
    private ViewTreeCommandHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        handler = new ViewTreeCommandHandler(repository);
    }

    @Test
    void testGetCommand() {
        assertEquals("/viewTree", handler.getCommand());
    }

    @Test
    void testHandleEmptyTree() {
        when(repository.findAllByParentIsNull()).thenReturn(Collections.emptyList());
        String result = handler.handle(new String[]{}, 1L);
        assertEquals("🌱 Дерево категорий пока пусто.", result);
    }

    @Test
    void testHandleTreeWithData() {
        Category root = new Category();
        root.setName("Root");

        Category child1 = new Category();
        child1.setName("Child1");
        child1.setParent(root);

        Category child2 = new Category();
        child2.setName("Child2");
        child2.setParent(root);

        root.setChildren(List.of(child1, child2));
        when(repository.findAllByParentIsNull()).thenReturn(List.of(root));

        String result = handler.handle(new String[]{}, 1L);
        String expected = """
                📂 Текущее дерево категорий:
                • Root
                  • Child1
                  • Child2
                """;
        assertEquals(expected, result);
    }
}
