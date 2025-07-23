package com.category_tree_bot.repository;

import com.category_tree_bot.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Category}.
 * Расширяет {@link JpaRepository}, предоставляя стандартные CRUD-операции,
 * а также кастомные методы поиска по имени, родителю и построения дерева.
 *
 * <p>Используется всеми обработчиками команд и сервисами,
 * связанными с управлением деревом категорий.</p>
 *
 * <p>Методы возвращают {@code Optional<Category>} или {@code List<Category>}
 * в зависимости от типа запроса.</p>
 *
 * @author Dinara
 * @see Category
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Ищет категорию по точному совпадению имени.
     *
     * @param name имя категории
     * @return {@code Optional} с категорией, если найдена
     */
    Optional<Category> findByName(String name);

    /**
     * Ищет корневую категорию (без родителя) по имени.
     *
     * @param name имя категории
     * @return {@code Optional} с категорией, если найдена
     */
    Optional<Category> findByNameAndParentIsNull(String name);

    /**
     * Ищет дочернюю категорию по имени и заданному родителю.
     *
     * @param name имя категории
     * @param parent родительская категория
     * @return {@code Optional} с категорией, если найдена
     */
    Optional<Category> findByNameAndParent(String name, Category parent);

    /**
     * Получает все корневые категории (у которых {@code parent = null}).
     *
     * @return список корневых категорий
     */
    List<Category> findAllByParentIsNull();

    /**
     * Ищет категорию по имени с предварительной загрузкой дочерних элементов (fetch join).
     *
     * <p>Используется для операций, где необходимо рекурсивное удаление
     * или отображение дерева категорий.</p>
     *
     * @param name имя категории
     * @return {@code Optional} с категорией и её дочерними категориями
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.name = :name")
    Optional<Category> findByNameWithChildren(@Param("name") String name);
}
