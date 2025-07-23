package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import com.category_tree_bot.service.CategoryDeletionService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработчик команды <b>/removeElement</b>, предназначенной для удаления категории и всех её подкатегорий.
 *
 * <p>Команда принимает один аргумент — имя удаляемой категории. Если категория существует,
 * она и все её дочерние элементы удаляются рекурсивно.</p>
 *
 * <p>Формат использования:
 * <pre>
 *     /removeElement НазваниеКатегории
 * </pre>
 * </p>
 *
 * @author Dinara
 * @see CommandHandler
 * @see CategoryDeletionService
 */
@Component
public class RemoveElementCommandHandler implements CommandHandler {

    private final CategoryRepository categoryRepository;
    private final CategoryDeletionService categoryDeletionService;

    /**
     * Конструктор обработчика команды удаления категории.
     *
     * @param categoryRepository       репозиторий для поиска категорий
     * @param categoryDeletionService  сервис для рекурсивного удаления категорий
     */
    public RemoveElementCommandHandler(CategoryRepository categoryRepository,
                                       CategoryDeletionService categoryDeletionService) {
        this.categoryRepository = categoryRepository;
        this.categoryDeletionService = categoryDeletionService;
    }

    /**
     * Возвращает команду, которую обрабатывает данный хендлер.
     *
     * @return строка <code>"/removeElement"</code>
     */
    @Override
    public String getCommand() {
        return "/removeElement";
    }

    /**
     * Обрабатывает удаление категории по имени, если она существует.
     * Категория удаляется вместе со всеми дочерними элементами.
     *
     * @param args   массив аргументов, где args[0] — имя категории
     * @param chatId идентификатор чата Telegram, из которого пришла команда
     * @return сообщение об успешном удалении или об ошибке
     */
    @Override
    public String handle(String[] args, Long chatId) {
        if (args.length < 1) {
            return "❌ Ошибка: укажите имя категории для удаления.";
        }

        String nameToDelete = args[0];

        Optional<Category> optionalCategory = categoryRepository.findByNameWithChildren(nameToDelete);

        if (optionalCategory.isEmpty()) {
            return "⚠️ Категория \"" + nameToDelete + "\" не найдена.";
        }

        categoryDeletionService.deleteRecursively(optionalCategory.get());

        return "🗑️ Категория \"" + nameToDelete + "\" и все её подкатегории были удалены.";
    }
}
