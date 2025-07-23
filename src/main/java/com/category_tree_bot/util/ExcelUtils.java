package com.category_tree_bot.util;

import com.category_tree_bot.entity.Category;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для работы с Excel-файлами (.xlsx), содержащими структуру категорий.
 *
 * <p>Использует библиотеку Apache POI для чтения данных из Excel. Предполагается,
 * что файл содержит таблицу с тремя колонками: название категории, её код и код родительской категории.</p>
 *
 * <p>Файл должен иметь следующий формат (первая строка — заголовок):</p>
 * <ul>
 *     <li>Столбец A: Название категории</li>
 *     <li>Столбец B: Код категории</li>
 *     <li>Столбец C: Код родительской категории</li>
 * </ul>
 *
 * <p>Каждая строка (начиная со второй) будет преобразована в объект {@link Category}.</p>
 *
 * @author Dinara
 * @see Category
 */
public class ExcelUtils {

    /**
     * Парсит Excel-файл и возвращает список категорий.
     *
     * <p>Пропускает первую строку, считая её заголовком. Каждая следующая строка должна содержать
     * название, код и код родителя. Если ячейки пустые или содержат некорректные значения, возможны ошибки выполнения.</p>
     *
     * @param file Excel-файл (.xlsx) с категориями
     * @return список категорий, извлечённых из файла
     * @throws Exception если возникает ошибка при чтении файла или его структуре
     */
    public static List<Category> parseExcelFile(File file) throws Exception {
        List<Category> categories = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Пропустить заголовок
                Category c = new Category();
                c.setName(row.getCell(0).getStringCellValue());
                c.setCode(row.getCell(1).getStringCellValue());
                c.setParentCode(row.getCell(2).getStringCellValue());
                categories.add(c);
            }
        }
        return categories;
    }
}
