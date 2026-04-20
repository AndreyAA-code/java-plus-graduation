#!/bin/bash

# Название: deploy.sh
# Описание: Скрипт для последовательного выполнения команд Docker Compose и Maven
# Запуск: ./deploy.sh

echo "🚀 Начинаем выполнение скрипта развёртывания..."

# 1. Останавливаем и удаляем контейнеры, сети, тома
echo "⏳ Шаг 1: Выполняем docker-compose down..."
if docker-compose down; then
    echo "✅ docker-compose down выполнен успешно."
else
    echo "❌ Ошибка при выполнении docker-compose down. Прекращаем выполнение."
    exit 1
fi

# 2. Выполняем сборку Maven: clean, validate, compile, test, package
echo "⏳ Шаг 2: Выполняем mvn clean validate compile test package..."
if mvn clean validate compile package -DskipTests; then
    echo "✅ Сборка Maven выполнена успешно."
else
    echo "❌ Ошибка при сборке Maven. Прекращаем выполнение."
    exit 1
fi

# 3. Собираем образы Docker на основе docker-compose.yml
echo "⏳ Шаг 3: Выполняем docker-compose build..."
if docker-compose build; then
    echo "✅ docker-compose build выполнен успешно."
else
    echo "❌ Ошибка при выполнении docker-compose build. Прекращаем выполнение."
    exit 1
fi

# 4. Запускаем контейнеры в фоновом режиме
echo "⏳ Шаг 4: Выполняем docker-compose up"
docker-compose up
