package com.category_tree_bot.configuration;

import com.category_tree_bot.bot.CategoryTreeBot;
import com.category_tree_bot.command.CommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

/**
 * Конфигурационный класс Spring, отвечающий за настройку Telegram-бота и связанных с ним компонентов.
 *
 * <ul>
 *     <li>Регистрирует Telegram-бота через {@link TelegramBotsApi}</li>
 *     <li>Определяет список всех командных обработчиков</li>
 * </ul>
 *
 * Использует Spring Bean-конфигурацию без Dotenv.
 *
 * @author Dinara
 * @see CategoryTreeBot
 * @see CommandHandler
 */
@Configuration
public class TelegramBotConfig {

    /**
     * Регистрирует Telegram-бота в Telegram API с использованием {@link DefaultBotSession}.
     *
     * @param bot экземпляр Telegram-бота
     * @return объект TelegramBotsApi с зарегистрированным ботом
     * @throws TelegramApiException если регистрация не удалась
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(CategoryTreeBot bot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }

    /**
     * Объединяет все обработчики команд в список.
     *
     * @param addElementCommandHandler    обработчик команды /addElement
     * @param removeElementCommandHandler обработчик команды /removeElement
     * @param viewTreeCommandHandler      обработчик команды /viewTree
     * @return список командных обработчиков
     */
    @Bean
    public List<CommandHandler> commandHandlers(
            CommandHandler addElementCommandHandler,
            CommandHandler removeElementCommandHandler,
            CommandHandler viewTreeCommandHandler
    ) {
        return List.of(
                addElementCommandHandler,
                removeElementCommandHandler,
                viewTreeCommandHandler
        );
    }
}
