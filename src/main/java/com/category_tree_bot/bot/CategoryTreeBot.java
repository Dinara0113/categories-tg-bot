package com.category_tree_bot.bot;

import com.category_tree_bot.command.CommandHandler;
import com.category_tree_bot.command.DownloadCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Telegram-бот для управления деревом категорий через команды и Excel-файлы.
 * Поддерживает загрузку дерева из Excel, выгрузку дерева в Excel и выполнение текстовых команд.
 *
 * <p>Использует Spring @Value для получения конфигурации из application.properties или .env</p>
 *
 * @author Dinara
 * @see CommandHandler
 * @see DownloadCommandHandler
 */
@Component
public class CategoryTreeBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(CategoryTreeBot.class);

    private final List<CommandHandler> handlers;
    private final DownloadCommandHandler downloadHandler;

    private final String username;
    private final String token;

    /**
     * Конструктор бота.
     *
     * @param handlers        список всех обработчиков команд
     * @param downloadHandler обработчик команды /download
     * @param username        имя Telegram-бота, берётся из конфигурации
     * @param token           токен Telegram-бота, берётся из конфигурации
     */
    public CategoryTreeBot(List<CommandHandler> handlers,
                           DownloadCommandHandler downloadHandler,
                           @Value("${telegram.bot.username}") String username,
                           @Value("${telegram.bot.token}") String token) {
        this.handlers = handlers;
        this.downloadHandler = downloadHandler;
        this.username = username;
        this.token = token;
    }

    /**
     * Получение имени Telegram-бота из конфигурации.
     *
     * @return имя Telegram-бота
     */
    @Override
    public String getBotUsername() {
        return username;
    }

    /**
     * Получение токена Telegram-бота из конфигурации.
     *
     * @return токен Telegram API
     */
    @Override
    public String getBotToken() {
        return token;
    }

    /**
     * Основной метод обработки входящих сообщений от Telegram.
     *
     * @param update объект обновления (сообщение, документ и т.д.)
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasText()) {
                String[] input = update.getMessage().getText().trim().split(" ");
                String command = input[0];

                if (command.equalsIgnoreCase("/download")) {
                    try {
                        byte[] fileBytes = downloadHandler.generateExcelFile();
                        InputFile file = new InputFile(new ByteArrayInputStream(fileBytes), "categories.xlsx");
                        SendDocument doc = new SendDocument(chatId.toString(), file);
                        execute(doc);
                    } catch (Exception e) {
                        logger.error("Ошибка при создании Excel-файла", e);
                        send(chatId, "❌ Ошибка при создании Excel-файла.");
                    }
                    return;
                }

                for (CommandHandler handler : handlers) {
                    if (handler.getCommand().equalsIgnoreCase(command)) {
                        String response = handler.handle(Arrays.copyOfRange(input, 1, input.length), chatId);
                        send(chatId, response);
                        return;
                    }
                }

                send(chatId, "⚠️ Неизвестная команда. Введите /help");
                return;
            }

            if (update.getMessage().hasDocument()) {
                Document document = update.getMessage().getDocument();
                String fileName = document.getFileName();

                if (fileName.endsWith(".xlsx")) {
                    try {
                        GetFile getFile = new GetFile(document.getFileId());
                        File tgFile = execute(getFile);
                        java.io.File localFile = downloadFile(tgFile);

                        String response = findHandler("/upload").handle(new String[]{}, chatId, localFile);
                        send(chatId, response);
                    } catch (Exception e) {
                        logger.error("Ошибка при загрузке Excel-файла", e);
                        send(chatId, "❌ Ошибка при загрузке Excel-файла.");
                    }
                } else {
                    send(chatId, "⚠️ Пожалуйста, отправьте Excel-файл с расширением .xlsx.");
                }
            }
        }
    }

    /**
     * Утилита для отправки простого текстового сообщения пользователю.
     *
     * @param chatId Telegram ID пользователя
     * @param text   текст сообщения
     */
    private void send(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения", e);
        }
    }

    /**
     * Поиск обработчика команды по её имени.
     *
     * @param command команда (например, "/upload")
     * @return обработчик команды
     * @throws IllegalArgumentException если команда не найдена
     */
    private CommandHandler findHandler(String command) {
        return handlers.stream()
                .filter(h -> h.getCommand().equalsIgnoreCase(command))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Хендлер не найден: " + command));
    }
}
