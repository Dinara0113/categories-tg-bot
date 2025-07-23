package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик команды <b>/viewTree</b>, предназначенной для отображения текущего дерева категорий.
 *
 * <p>Команда рекурсивно обходит все категории, начиная с корневых (у которых нет родителя),
 * и строит текстовое представление дерева в формате списка с отступами.</p>
 *
 * <p>Каждая подкатегория отображается с соответствующим уровнем вложенности,
 * что позволяет визуально воспринять иерархию категорий.</p>
 *
 * <p>Если дерево пустое, пользователю возвращается сообщение об отсутствии категорий.</p>
 *
 * @author Dinara
 * @see CommandHandler
 * @see CategoryRepository
 */
@Component
public class ViewTreeCommandHandler implements CommandHandler {

    private final CategoryRepository categoryRepository;

    /**
     * Конструктор, инициализирующий репозиторий для работы с категориями.
     *
     * @param categoryRepository репозиторий категорий
     */
    public ViewTreeCommandHandler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Возвращает имя команды, обрабатываемой данным хендлером.
     *
     * @return строка <code>"/viewTree"</code>
     */
    @Override
    public String getCommand() {
        return "/viewTree";
    }

    /**
     * Обрабатывает команду <b>/viewTree</b> и возвращает строковое представление дерева категорий.
     *
     * @param args   аргументы команды (игнорируются)
     * @param chatId идентификатор чата Telegram
     * @return текстовое представление дерева категорий или сообщение о его пустоте
     */
    @Override
    @Transactional
    public String handle(String[] args, Long chatId) {
        List<Category> roots = categoryRepository.findAllByParentIsNull();

        if (roots.isEmpty()) {
            return "🌱 Дерево категорий пока пусто.";
        }

        StringBuilder sb = new StringBuilder("📂 Текущее дерево категорий:\n");
        for (Category root : roots) {
            buildTreeString(root, sb, 0);
        }

        return sb.toString();
    }

    /**
     * Рекурсивно строит строковое представление дерева, добавляя отступы
     * в зависимости от уровня вложенности.
     *
     * @param category категория, с которой начинается построение
     * @param sb       строковый буфер, в который добавляется результат
     * @param level    уровень вложенности текущей категории
     */
    private void buildTreeString(Category category, StringBuilder sb, int level) {
        sb.append("  ".repeat(level)).append("• ").append(category.getName()).append("\n");
        for (Category child : category.getChildren()) {
            buildTreeString(child, sb, level + 1);
        }
    }
}
