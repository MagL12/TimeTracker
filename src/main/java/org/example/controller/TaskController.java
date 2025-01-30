package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Task;
import org.example.service.TaskService;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Контроллер для управления задачами через пользовательский интерфейс.
 * Обеспечивает взаимодействие с пользователем и передачу команд в сервис задач.
 */
public class TaskController {
    private TaskService taskService; // Сервис для работы с задачами
    private Scanner scanner; // Сканер для ввода данных от пользователя

    private static final Logger logger = LogManager.getLogger(TaskController.class);

    /**
     * Конструктор для создания экземпляра TaskController.
     *
     * @param taskService сервис для работы с задачами
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
        this.scanner = new Scanner(System.in);
        logger.info("TaskController initialized");
    }

    /**
     * Запускает приложение и отображает меню для взаимодействия с пользователем.
     * Пользователь может добавлять, просматривать, обновлять, удалять, останавливать и завершать задачи.
     */
    public void run() {
        logger.info("Starting application...");
        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Добавить задачу");
            System.out.println("2. Показать все задачи");
            System.out.println("3. Изменить название задачи");
            System.out.println("4. Удалить задачу");
            System.out.println("5. Остановить задачу");
            System.out.println("6. Завершить задачу");
            System.out.println("7. Выход");
            System.out.println("8. Справка");

            try {
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    switch (choice) {
                        case 1:
                            addTask();
                            break;
                        case 2:
                            showAllTasks();
                            break;
                        case 3:
                            updateTaskName();
                            break;
                        case 4:
                            deleteTask();
                            break;
                        case 5:
                            stopTask();
                            break;
                        case 6:
                            finishTask();
                            break;
                        case 7:
                            logger.info("Exiting application...");
                            return;
                        case 8:
                            printHelp();
                            break;
                        default:
                            System.out.println("Неверный выбор.");
                    }
                } else {
                    throw new NoSuchElementException("Expected an integer but found other input");
                }
            } catch (NoSuchElementException e) {
                logger.error("Error reading user input", e);
                System.out.println("Ошибка при вводе данных. Пожалуйста, повторите ввод.");
                scanner.nextLine(); // clear the invalid input
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                e.printStackTrace();
                System.out.println("Произошла непредвиденная ошибка. Пожалуйста, повторите ввод.");
                scanner.nextLine(); // clear the invalid input
            }
        }
    }

    /**
     * Добавляет новую задачу с именем, введенным пользователем.
     */
    private void addTask() {
        System.out.print("Введите название задачи: ");
        String name = scanner.nextLine();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Название задачи не может быть пустым");
            logger.error("Error adding task: name is empty");
            return;
        }
        Optional<Long> taskId = taskService.addTask(name);
        if (taskId.isPresent()) {
            System.out.println("Задача добавлена. ID задачи: " + taskId.get());
            logger.info("Task added with ID: {}", taskId.get());
        } else {
            System.out.println("Ошибка при добавлении задачи.");
            logger.error("Error adding task");
        }
    }

    /**
     * Отображает список всех задач.
     */
    private void showAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("Нет активных задач.");
            logger.info("No active tasks found");
        } else {
            // Форматирование времени (без секунд)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            System.out.println("+----+-----------------------------+----------------------+---------------------+-------------------------+");
            System.out.println("| ID | Название                    | Начало              | Статус              | Время выполнения         |");
            System.out.println("+----+-----------------------------+----------------------+---------------------+-------------------------+");
            for (Task task : tasks) {
                System.out.printf(
                        "| %-2d | %-27s | %-19s | %-19s | %-24s |\n",
                        task.getId(),
                        task.getName(),
                        task.getStartTime().format(formatter), // Форматируем время
                        task.getStatus(),
                        getDurationString(task)
                );
            }
            System.out.println("+----+-----------------------------+---------------------+---------------------+--------------------------+");
            logger.info("Displayed all tasks");
        }
    }

    /**
     * Обновляет название задачи по её ID.
     */
    private void updateTaskName() {
        System.out.print("Введите ID задачи: ");
        long idInput;
        try {
            idInput = scanner.nextLong();
            scanner.nextLine(); // Очистка буфера после nextLong()
        } catch (InputMismatchException e) {
            System.out.println("Неверный формат ID.");
            scanner.nextLine(); // Очистка буфера в случае ошибки
            return;
        }

        long taskId = idInput;

        System.out.print("Введите новое название задачи: ");
        String newName = scanner.nextLine();
        if (newName == null || newName.trim().isEmpty()) {
            System.out.println("Название задачи не может быть пустым");
            return;
        }

        if (taskService.updateTaskName(taskId, newName)) {
            System.out.println("Название задачи успешно изменено.");
        } else {
            System.out.println("Не удалось изменить название задачи.");
        }
    }

    /**
     * Удаляет задачу по её ID.
     */
    private void deleteTask() {
        System.out.print("Введите ID задачи: ");
        long idInput = scanner.nextLong();
        scanner.nextLine();
        long taskId;
        try {
            taskId = idInput;
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID.");
            return;
        }

        if (taskService.deleteTask(taskId)) {
            System.out.println("Задача успешно удалена.");
        } else {
            System.out.println("Не удалось удалить задачу.");
        }
    }

    /**
     * Останавливает задачу по её ID.
     */
    private void stopTask() {
        System.out.print("Введите ID задачи: ");
        long idInput = scanner.nextLong();
        scanner.nextLine();
        long taskId;
        try {
            taskId = idInput;
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID.");
            return;
        }

        if (taskService.stopTask(taskId)) {
            System.out.println("Задача успешно остановлена.");
        } else {
            System.out.println("Не удалось остановить задачу.");
        }
    }

    /**
     * Завершает задачу по её ID.
     */
    private void finishTask() {
        System.out.print("Введите ID задачи: ");
        long idInput = scanner.nextLong();
        scanner.nextLine();
        long taskId;
        try {
            taskId = idInput;
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID.");
            return;
        }

        if (taskService.finishTask(taskId)) {
            System.out.println("Задача успешно завершена.");
        } else {
            System.out.println("Не удалось завершить задачу.");
        }
    }

    /**
     * Возвращает строковое представление продолжительности выполнения задачи.
     *
     * @param task задача, для которой вычисляется продолжительность
     * @return строка в формате "X час(ов) Y минут(ы)"
     */
    private String getDurationString(Task task) {
        long totalMinutes = taskService.getDuration(task).toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d час(ов) %d минут(ы)", hours, minutes);
    }

    /**
     * Выводит справку по доступным командам.
     */
    private void printHelp() {
        System.out.println("-----------------------------------------------------------------------.");
        System.out.println("Доступные команды:                                                     |");
        System.out.println("1. Добавить задачу - Создает новую задачу.                             |");
        System.out.println("2. Показать все задачи - Отображает список всех задач.                 |");
        System.out.println("3. Изменить название задачи - Обновляет название задачи по её ID.      |");
        System.out.println("4. Удалить задачу - Удаляет задачу по её ID.                           |");
        System.out.println("5. Остановить задачу - Останавливает выполнение задачи по её ID.       |");
        System.out.println("6. Завершить задачу - Завершает задачу по её ID.                       |");
        System.out.println("7. Выход - Завершает работу приложения.                                |");
        System.out.println("8. Справка - Выводит список доступных команд.                          |");
        System.out.println("------------------------------------------------------------------------");
    }
}