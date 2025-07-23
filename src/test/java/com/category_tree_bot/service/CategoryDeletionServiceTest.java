package com.category_tree_bot.service;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class CategoryDeletionServiceTest {

    private CategoryRepository repository;
    private CategoryDeletionService service;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        service = new CategoryDeletionService(repository);
    }

    @Test
    void testDeleteRecursively_NoChildren() {
        Category category = new Category();
        category.setChildren(List.of());

        service.deleteRecursively(category);

        verify(repository).delete(category);
    }

    @Test
    void testDeleteRecursively_WithChildren() {
        Category child1 = new Category();
        child1.setChildren(List.of());

        Category child2 = new Category();
        child2.setChildren(List.of());

        Category parent = new Category();
        parent.setChildren(List.of(child1, child2));

        service.deleteRecursively(parent);

        verify(repository).delete(child1);
        verify(repository).delete(child2);
        verify(repository).delete(parent);
    }
}
