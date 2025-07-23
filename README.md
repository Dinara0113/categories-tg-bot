📂 Category Tree Bot

Category Tree Bot — это Telegram-бот на Spring Boot и PostgreSQL для управления иерархической структурой категорий. Поддерживает добавление, удаление, отображение дерева категорий и загрузку/выгрузку в Excel.

🚀 Функциональность

- 📌 Добавление новой категории (с указанием родителя)
- 🧾 Отображение дерева категорий
- ❌ Удаление категории и всех её подкатегорий
- 📥 Импорт категорий из Excel
- 📤 Экспорт дерева категорий в Excel

---

🛠️ Технологии

- Java 17
- Spring Boot 3.3
- TelegramBots Spring Boot Starter (`6.8.0`)
- JPA / Hibernate
- PostgreSQL
- Apache POI — для работы с Excel-файлами

---
 📦 Установка и запуск

1. Клонируй репозиторий

2. Создай файл src/main/resources/application.properties и укажи параметры:
spring.datasource.url=jdbc:postgresql://localhost:5432/category_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
telegram.bot.username=YourBotUsername
telegram.bot.token=123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11


3. Запусти PostgreSQL
docker run --name category-postgres -e POSTGRES_PASSWORD=your_password -e POSTGRES_DB=category_db -p 5432:5432 -d postgres

5. Запусти приложение
./mvnw spring-boot:run


🧠 Архитектура
CommandHandler — интерфейс для всех команд

CategoryTreeBot — основной Telegram-бот

CategoryService, CategoryDeletionService — бизнес-логика

UploadHandler, DownloadHandler — работа с Excel

CategoryRepository — JPA-репозиторий


🛡️ Обработка ошибок
Валидируются входные команды

Обрабатываются ситуации, когда категории не найдены

Учитываются проблемы с Excel-файлами

Hibernate Lazy-loading решён с помощью @Query(fetch) и @Transactional

