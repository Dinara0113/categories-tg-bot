package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Обработчик команды <b>/download</b>, отвечающей за экспорт дерева категорий в Excel-файл.
 *
 * <p>Формирует Excel-файл с перечнем всех категорий, включая их ID, названия и имена родительских категорий.
 * Экспортируемый файл отправляется пользователю ботом вручную из класса {@link com.category_tree_bot.bot.CategoryTreeBot}.</p>
 *
 * <p>Файл содержит следующие колонки:
 * <ul>
 *     <li><b>ID</b> — уникальный идентификатор категории</li>
 *     <li><b>Название</b> — имя категории</li>
 *     <li><b>Родитель</b> — имя родительской категории или «—» для корневых</li>
 * </ul>
 * </p>
 *
 * @author Dinara
 * @see com.category_tree_bot.bot.CategoryTreeBot
 * @see com.category_tree_bot.util.ExcelUtils
 */
@Component
public class DownloadCommandHandler implements CommandHandler {

    private final CategoryRepository categoryRepository;

    /**
     * Конструктор для внедрения репозитория категорий.
     *
     * @param categoryRepository репозиторий для получения всех категорий
     */
    public DownloadCommandHandler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Возвращает название команды, которую обрабатывает данный хендлер.
     *
     * @return строка <code>"/download"</code>
     */
    @Override
    public String getCommand() {
        return "/download";
    }

    /**
     * Обработка команды не используется напрямую, так как бот вручную отправляет ответ с Excel-файлом.
     *
     * @param args   аргументы команды
     * @param chatId ID Telegram-чата
     * @return всегда возвращает {@code null}, т.к. ответ обрабатывается вручную в {@link com.category_tree_bot.bot.CategoryTreeBot}
     */
    @Override
    public String handle(String[] args, Long chatId) {
        return null;
    }

    /**
     * Генерирует Excel-файл с текущим списком категорий из базы данных.
     *
     * @return массив байтов, представляющий сгенерированный .xlsx файл
     * @throws IOException если происходит ошибка при создании файла
     */
    public byte[] generateExcelFile() throws IOException {
        List<Category> categories = categoryRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Categories");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Название");
            header.createCell(2).setCellValue("Родитель");

            int rowIndex = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getParent() != null
                        ? category.getParent().getName()
                        : "—");
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
