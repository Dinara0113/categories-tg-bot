package com.category_tree_bot.command;

import org.springframework.stereotype.Component;

/**
 * Обработчик команды <b>/start</b>, которая используется при первом запуске бота.
 *
 * <p>Возвращает приветственное сообщение и предлагает пользователю ввести команду <code>/help</code>
 * для получения списка всех доступных команд.</p>
 *
 * <p>Команда не требует аргументов и служит точкой входа в функциональность бота.</p>
 *
 * @author Dinara
 * @see CommandHandler
 */
@Component
public class StartCommandHandler implements CommandHandler {

    /**
     * Возвращает название команды, которую обрабатывает данный хендлер.
     *
     * @return строка <code>"/start"</code>
     */
    @Override
    public String getCommand() {
        return "/start";
    }

    /**
     * Обрабатывает команду <code>/start</code> и возвращает приветственное сообщение.
     *
     * @param args   массив аргументов команды (не используется)
     * @param chatId идентификатор Telegram-чата, откуда была вызвана команда
     * @return приветственное сообщение с подсказкой о команде <code>/help</code>
     */
    @Override
    public String handle(String[] args, Long chatId) {
        return "👋 Привет! Я бот для управления деревом категорий. Введите /help, чтобы узнать доступные команды.";
    }
}
