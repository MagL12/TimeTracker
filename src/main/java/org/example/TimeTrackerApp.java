package org.example;

import org.example.controller.TaskController;
import org.example.dao.TaskDAO;
import org.example.service.TaskService;

/**
 * Главный класс приложения TimeTrackerApp, который инициализирует и запускает контроллер задач.
 */
public class TimeTrackerApp {
    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // Создание экземпляра TaskDAO для взаимодействия с базой данных
        TaskDAO taskDAO = new TaskDAO();

        // Создание экземпляра TaskService с использованием TaskDAO
        TaskService taskService = new TaskService(taskDAO);

        // Создание экземпляра TaskController с использованием TaskService
        TaskController taskController = new TaskController(taskService);

        // Запуск контроллера задач
        taskController.run();
    }
}
