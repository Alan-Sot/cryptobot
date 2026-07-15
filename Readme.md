# 🤖 CryptoBot

Telegram-бот для запроса цен криптовалют.

## Возможности

* Запрос цены BTC и ETH
* Подписка на отслеживание BTC и ETH с обновлением цены раз в минуту
* Хранение подписок в базе данных PostgreSQL
* REST API для управления подписками
* Запуск через Docker Compose

## Технологии

* Java 21
* Spring Boot 3
* Spring Data JPA
* PostgreSQL
* Telegram Bots API
* Docker
* Maven
* JUnit 5

## Запуск через Docker

### Требования

* Docker
* Docker Compose

### Сборка и запуск

```bash
  docker-compose up --build
```

После запуска будут доступны:

* Приложение: http://localhost:8080
* PostgreSQL: localhost:5432

## Конфигурация

Настройки приложения задаются через переменные окружения:

| Переменная   | Описание            |
| ------------ |---------------------|
| POSTGRES_DB | название БД         |
| POSTGRES_USER | Имя пользователя БД |
| POSTGRES_PASSWORD | Пароль БД           |
| SPRING_DATASOURCE_URL | URL базы данных     |
| SPRING_DATASOURCE_USERNAME | Пользователь БД     |
| SPRING_DATASOURCE_PASSWORD | Пароль БД           |
| BOT_TOKEN    | Токен Telegram-бота |
| BOT_USERNAME | Имя Telegram-бота   |

## Тестирование

Запуск всех тестов:

```bash
  mvn test
```

## Docker Compose

Для запуска используются два контейнера:

* PostgreSQL
* CryptoBot Application

Команда остановки:

```bash
  docker-compose down
```

Удаление контейнеров и томов:

```bash
  docker-compose down -v
```

## Автор

Разработчик: github/Alan-Sot
