package com.category_tree_bot.command;

import com.category_tree_bot.util.ExcelUtils;
import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Обработчик команды <b>/upload</b>, предназначенной для загрузки дерева категорий из Excel-файла.
 *
 * <p>Поддерживает загрузку Excel-документа формата .xlsx, содержащего структуру категорий
 * с колонками: "Название", "Код", "Родительский код".</p>
 *
 * <p>Файл загружается пользователем через Telegram-чат, после чего бот парсит его и сохраняет
 * все категории в базу данных.</p>
 *
 * @author Dinara
 * @see CommandHandler
 * @see ExcelUtils
 */
@Component
public class UploadCommandHandler implements CommandHandler {

    private final CategoryRepository repository;

    /**
     * Конструктор, инициализирующий репозиторий категорий.
     *
     * @param repository репозиторий для сохранения загружаемых категорий
     */
    public UploadCommandHandler(CategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Возвращает команду, с которой ассоциирован данный обработчик.
     *
     * @return строка <code>"/upload"</code>
     */
    @Override
    public String getCommand() {
        return "/upload";
    }

    /**
     * Стандартный обработчик команды. В данном случае не используется, поскольку загрузка
     * осуществляется через файл.
     *
     * @param args   аргументы команды (игнорируются)
     * @param chatId идентификатор чата Telegram
     * @return пустая строка
     */
    @Override
    public String handle(String[] args, Long chatId) {
        return "";
    }

    /**
     * Обрабатывает загруженный Excel-файл: парсит его, извлекает список категорий и сохраняет их в БД.
     *
     * @param args   аргументы команды (игнорируются)
     * @param chatId идентификатор Telegram-чата
     * @param file   файл Excel, загруженный пользователем
     * @return сообщение об успехе или ошибке загрузки
     */
    @Override
    public String handle(String[] args, Long chatId, File file) {
        try {
            List<Category> categories = ExcelUtils.parseExcelFile(file);
            repository.saveAll(categories);
            return "✅ Данные из Excel успешно загружены.";
        } catch (Exception e) {
            return "❌ Ошибка при загрузке Excel: " + e.getMessage();
        }
    }
}
