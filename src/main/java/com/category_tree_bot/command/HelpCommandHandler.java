package com.category_tree_bot.command;

import org.springframework.stereotype.Component;

/**
 * Обработчик команды <b>/help</b>, предоставляющий справочную информацию о доступных командах бота.
 *
 * <p>Возвращает список всех поддерживаемых команд Telegram-бота с кратким описанием каждой.</p>
 *
 * <p>Команды, перечисленные в справке:
 * <ul>
 *     <li><code>/addElement Название</code> — добавить корневой элемент</li>
 *     <li><code>/addElement Родитель Дочерний</code> — добавить подкатегорию</li>
 *     <li><code>/viewTree</code> — показать дерево категорий</li>
 *     <li><code>/removeElement Название</code> — удалить элемент вместе с подкатегориями</li>
 *     <li><code>/download</code> — скачать дерево категорий в Excel</li>
 *     <li><code>/upload</code> — загрузить дерево из Excel-файла</li>
 *     <li><code>/help</code> — вывести данное справочное сообщение</li>
 * </ul>
 * </p>
 *
 * @author Dinara
 * @see com.category_tree_bot.command.CommandHandler
 */
@Component
public class HelpCommandHandler implements CommandHandler {

    /**
     * Возвращает команду, обрабатываемую этим хендлером.
     *
     * @return строка <code>"/help"</code>
     */
    @Override
    public String getCommand() {
        return "/help";
    }

    /**
     * Возвращает справочное сообщение со списком всех доступных команд и их описанием.
     *
     * @param args   аргументы команды (игнорируются)
     * @param chatId идентификатор Telegram-чата, из которого поступила команда
     * @return текст справки для отображения пользователю
     */
    @Override
    public String handle(String[] args, Long chatId) {
        return """
                🤖 Доступные команды:
                /addElement <название> — добавить корневой элемент
                /addElement <родитель> <дочерний> — добавить подкатегорию
                /viewTree — показать дерево категорий
                /removeElement <название> — удалить элемент с подкатегориями
                /download — скачать дерево в Excel
                /upload — загрузить дерево из Excel
                /help — показать это сообщение
                """;
    }
}
