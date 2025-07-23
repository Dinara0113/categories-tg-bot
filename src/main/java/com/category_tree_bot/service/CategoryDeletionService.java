package com.category_tree_bot.service;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для рекурсивного удаления категорий вместе с их подкатегориями.
 *
 * <p>Используется в {@link com.category_tree_bot.command.RemoveElementCommandHandler}
 * при выполнении команды удаления. Сервис гарантирует, что сначала удаляются все дочерние категории,
 * а затем — сама родительская.</p>
 *
 * <p>Операция удаления выполняется в рамках одной транзакции благодаря аннотации {@link Transactional},
 * что обеспечивает целостность данных.</p>
 *
 * <p>Удаление производится напрямую через {@link CategoryRepository}.</p>
 *
 * @author Dinara
 * @see com.category_tree_bot.command.RemoveElementCommandHandler
 * @see Category
 * @see CategoryRepository
 */
@Service
public class CategoryDeletionService {

    private final CategoryRepository categoryRepository;

    /**
     * Конструктор с внедрением репозитория категорий.
     *
     * @param categoryRepository репозиторий для удаления категорий
     */
    public CategoryDeletionService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Рекурсивно удаляет указанную категорию и все её подкатегории.
     *
     * <p>Сначала вызывается удаление всех дочерних элементов, а затем удаляется сам элемент.
     * Метод предполагает, что все дочерние категории уже загружены (fetch).</p>
     *
     * @param category корневая категория, с которой начинается удаление
     */
    @Transactional
    public void deleteRecursively(Category category) {
        for (Category child : category.getChildren()) {
            deleteRecursively(child);
        }
        categoryRepository.delete(category);
    }
}
