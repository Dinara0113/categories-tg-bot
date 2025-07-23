package com.category_tree_bot.command;

import com.category_tree_bot.entity.Category;
import com.category_tree_bot.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AddElementCommandHandler implements CommandHandler {

    private final CategoryRepository repository;

    public AddElementCommandHandler(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getCommand() {
        return "/addElement";
    }

    @Override
    public String handle(String[] args, Long chatId) {
        // Объединяем весь массив в строку, чтобы парсить кавычки
        String joined = String.join(" ", args);
        List<String> parsedArgs = parseArgs(joined);

        if (parsedArgs.isEmpty()) {
            return "❌ Ошибка: укажите хотя бы одно имя категории.";
        }

        if (parsedArgs.size() == 1) {
            String name = parsedArgs.get(0);
            Optional<Category> existing = repository.findByNameAndParentIsNull(name);
            if (existing.isPresent()) {
                return "⚠️ Такая корневая категория уже существует.";
            }
            Category category = new Category();
            category.setName(name);
            category.setParent(null);
            repository.save(category);
            return "✅ Корневая категория '" + name + "' успешно добавлена.";
        }

        if (parsedArgs.size() == 2) {
            String parentName = parsedArgs.get(0);
            String childName = parsedArgs.get(1);

            Optional<Category> parent = repository.findByNameAndParentIsNull(parentName);
            if (parent.isEmpty()) {
                return "❌ Родительская категория '" + parentName + "' не найдена.";
            }

            Optional<Category> existingChild = repository.findByNameAndParent(childName, parent.get());
            if (existingChild.isPresent()) {
                return "⚠️ Подкатегория '" + childName + "' уже существует в '" + parentName + "'.";
            }

            Category child = new Category();
            child.setName(childName);
            child.setParent(parent.get());
            repository.save(child);
            return "✅ Подкатегория '" + childName + "' добавлена в '" + parentName + "'.";
        }

        return "❌ Неверный формат команды. Используйте:\n" +
                "/addElement \"Название\"\n" +
                "или\n" +
                "/addElement \"Родитель\" \"Подкатегория\"";
    }

    // Метод для парсинга аргументов с поддержкой кавычек
    private List<String> parseArgs(String text) {
        List<String> result = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(text);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                result.add(matcher.group(1));
            } else {
                result.add(matcher.group(2));
            }
        }
        return result;
    }
}
