# Time Tracker

## Описание

Time Tracker — это консольное приложение для отслеживания времени, затраченного на выполнение задач. Приложение позволяет пользователю управлять несколькими одновременно выполняемыми задачами и отслеживать время, затраченное на каждую из них, в режиме реального времени.

## Функции

- Добавление задачи
- Отслеживание задач
- Изменение названия задачи
- Удаление задачи
- Остановка и завершение задачи
- Хранение данных в SQL базе данных

## Установка

### Требования

- Docker

### Шаги установки

1. **Установите Docker и Docker Compose**:
    - Скачайте и установите Docker с [официального сайта Docker](https://www.docker.com/products/docker-desktop).
    - Убедитесь, что Docker Compose установлен вместе с Docker Desktop.
2. **Запустить Docker Desktop**:

3. **Клонируйте репозиторий**:

   ```sh
   git clone https://github.com/MagL12/TimeTracker.git

   
### Запуск приложения

1. **Перейдете в дерикторию проекта TimeTracker**:

2. **Вам необходимо скопировать путь к папки TimeTracker**: Пример - D:\TimeTracker

3. **Откройте terminal и введите команду**: 
    ```sh
   cd D:\TimeTracker 
   docker-compose up
4. **Откройте еще один terminal и введите команду** 
   ```sh
   docker exec -it time_tracker /bin/sh
   
5. **Вы внутри контейнера** 
Докозательство этому у вас в командной строке вы увидете /app $
  
6. **Запуск приложения**:
   ```sh
   java -jar app.jar

#### Приложение готово к использованию 

### Команды для работы с приложением 

1. Добавить задачу - Создает новую задачу.
2. Показать все задачи - Отображает список всех задач.
3. Изменить название задачи - Обновляет название задачи по её ID.
4. Удалить задачу - Удаляет задачу по её ID.
5. Остановить задачу - Останавливает выполнение задачи по её ID.
6. Завершить задачу - Завершает задачу по её ID.
7. Выход - Завершает работу приложения.
8. Справка - Выводит список доступных команд.