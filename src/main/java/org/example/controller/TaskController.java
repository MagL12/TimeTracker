package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Task;
import org.example.service.TaskService;

import java.time.Duration;
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
        Optional<UUID> taskId = taskService.addTask(name);
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
            for (Task task : tasks) {
                System.out.println("ID: " + task.getId());
                System.out.println("Название: " + task.getName());
                System.out.println("Начало: " + task.getStartTime());
                System.out.println("Статус: " + task.getStatus());
                System.out.println("Время выполнения: " + getDurationString(task));
                System.out.println("-----------------------------");
            }
            logger.info("Displayed all tasks");
        }
    }

    /**
     * Обновляет название задачи по её ID.
     */
    private void updateTaskName() {
        System.out.print("Введите ID задачи: ");
        String idInput = scanner.nextLine();
        UUID taskId;
        try {
            taskId = UUID.fromString(idInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID.");
            logger.warn("Invalid UUID format: {}", idInput);
            return;
        }

        System.out.print("Введите новое название задачи: ");
        String newName = scanner.nextLine();
        if (newName == null || newName.trim().isEmpty()) {
            System.out.println("Название задачи не может быть пустым");
            logger.warn("Failed to update task name for ID: {}", taskId);
            return;
        }
        if (taskService.updateTaskName(taskId, newName)) {
            System.out.println("Название задачи успешно изменено.");
            logger.info("Task name updated for ID: {}", taskId);
        } else {
            System.out.println("Не удалось изменить название задачи.");
            logger.warn("Failed to update task name for ID: {}", taskId);
        }
    }

    /**
     * Удаляет задачу по её ID.
     */
    private void deleteTask() {
        System.out.print("Введите ID задачи: ");
        String idInput = scanner.nextLine();
        UUID taskId;
        try {
            taskId = UUID.fromString(idInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID.");
            logger.warn("Invalid UUID format: {}", idInput);
            return;
        }

        if (taskService.deleteTask(taskId)) {
            System.out.println("Задача успешно удалена.");
            logger.info("Task deleted with ID: {}", taskId);
        } else {
            System.out.println("Не удалось удалить задачу.");
            logger.warn("Failed to delete task with ID: {}", taskId);
        }
    }

    /**
     * Останавливает задачу по её ID.
     */
    private void stopTask() {
        System.out.print("Введите ID задачи: ");
        String idInput = scanner.nextLine();
        UUID taskId;
        try {
            taskId = UUID.fromString(idInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID.");
            logger.warn("Invalid UUID format: {}", idInput);
            return;
        }

        if (taskService.stopTask(taskId)) {
            System.out.println("Задача успешно остановлена.");
            logger.info("Task stopped with ID: {}", taskId);
        } else {
            System.out.println("Не удалось остановить задачу.");
            logger.warn("Failed to stop task with ID: {}", taskId);
        }
    }

    /**
     * Завершает задачу по её ID.
     */
    private void finishTask() {
        System.out.print("Введите ID задачи: ");
        String idInput = scanner.nextLine();
        UUID taskId;
        try {
            taskId = UUID.fromString(idInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID.");
            logger.warn("Invalid UUID format: {}", idInput);
            return;
        }

        if (taskService.finishTask(taskId)) {
            System.out.println("Задача успешно завершена.");
            logger.info("Task finished with ID: {}", taskId);
        } else {
            System.out.println("Не удалось завершить задачу.");
            logger.warn("Failed to finish task with ID: {}", taskId);
        }
    }

    /**
     * Возвращает строковое представление продолжительности выполнения задачи.
     *
     * @param task задача, для которой вычисляется продолжительность
     * @return строка в формате "X час(ов) Y минут(ы)"
     */
    private String getDurationString(Task task) {
        Duration duration = task.getDuration();
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d час(ов) %d минут(ы)", hours, minutes);
    }
}