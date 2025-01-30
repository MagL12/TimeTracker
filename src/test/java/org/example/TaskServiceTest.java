package org.example;

import org.example.dao.TaskDAO;
import org.example.entity.Task;
import org.example.exception.TaskNotFoundException;
import org.example.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Класс для тестирования сервиса задач (TaskService).
 */
public class TaskServiceTest {
    @Mock
    private TaskDAO taskDAO; // Мок для TaskDAO

    @InjectMocks
    private TaskService taskService; // Сервис, который тестируем

    /**
     * Инициализация моков перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    /**
     * Тестируем добавление новой задачи.
     */
    @Test
    @DisplayName("Тестируем добавление новой задачи")
    void testAddTask() {
        // Подготовка данных
        String taskName = "Test Task";
        long taskId = 1;
        Task task = new Task();
        task.setName(taskName);
        task.setStartTime(LocalDateTime.now());
        task.setStatus("Активна");

        // Мокируем поведение TaskDAO
        when(taskDAO.addTask(any(Task.class))).thenReturn(Optional.of(taskId));

        // Вызов метода
        Optional<Long> result = taskService.addTask(taskName);

        // Проверки
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get());
        verify(taskDAO, times(1)).addTask(any(Task.class));
    }

    /**
     * Тестируем правильное ли количество задач возвращается.
     */
    @Test
    @DisplayName("Тестируем правильное ли количество задач возвращается")
    void testGetAllTasks() {
        // Подготовка данных
        Task task1 = new Task();
        task1.setName("Task 1");
        Task task2 = new Task();
        task2.setName("Task 2");
        List<Task> tasks = Arrays.asList(task1, task2);

        // Мокируем поведение TaskDAO
        when(taskDAO.getAllTasks()).thenReturn(tasks);

        // Вызов метода
        List<Task> result = taskService.getAllTasks();

        // Проверки
        assertEquals(2, result.size());
        verify(taskDAO, times(1)).getAllTasks();
    }

    /**
     * Тестируем изменение названия существующей задачи.
     */
    @Test
    @DisplayName("Тестируем изменение названия существующей задачи")
    void testUpdateTaskName_Success() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 1;
        String newName = "Updated Task Name";

        // Мокируем поведение TaskDAO
        when(taskDAO.updateTaskName(taskId, newName)).thenReturn(true);

        // Вызов метода
        boolean result = taskService.updateTaskName(taskId, newName);

        // Проверки
        assertTrue(result);
        verify(taskDAO, times(1)).updateTaskName(taskId, newName);
    }

    /**
     * Тестируем изменение названия несуществующей задачи.
     */
    @Test
    @DisplayName("Тестируем изменение названия несуществующей задачи")
    void testUpdateTaskName_TaskNotFound() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 2;
        String newName = "Updated Task Name";

        // Мокируем поведение TaskDAO
        when(taskDAO.updateTaskName(taskId, newName)).thenThrow(new TaskNotFoundException(taskId));

        // Вызов метода и проверка исключения
        assertFalse(taskService.updateTaskName(taskId, newName));
        verify(taskDAO, times(1)).updateTaskName(taskId, newName);
    }

    /**
     * Тестируем удаление существующей задачи.
     */
    @Test
    @DisplayName("Тестируем удаление существующей задачи")
    void testDeleteTask_Success() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 1;

        // Мокируем поведение TaskDAO
        when(taskDAO.deleteTask(taskId)).thenReturn(true);

        // Вызов метода
        boolean result = taskService.deleteTask(taskId);

        // Проверки
        assertTrue(result);
        verify(taskDAO, times(1)).deleteTask(taskId);
    }

    /**
     * Тестируем удаление несуществующей задачи.
     */
    @Test
    @DisplayName("Тестируем удаление несуществующей задачи")
    void testDeleteTask_TaskNotFound() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 2;

        // Мокируем поведение TaskDAO
        when(taskDAO.deleteTask(taskId)).thenThrow(new TaskNotFoundException(taskId));

        // Вызов метода и проверка исключения
        assertFalse(taskService.deleteTask(taskId));
        verify(taskDAO, times(1)).deleteTask(taskId);
    }

    /**
     * Тестируем остановку существующей задачи.
     */
    @Test
    @DisplayName("Тестируем остановку существующей задачи")
    void testStopTask_Success() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 1;

        // Мокируем поведение TaskDAO
        when(taskDAO.stopTask(taskId)).thenReturn(true);

        // Вызов метода
        boolean result = taskService.stopTask(taskId);

        // Проверки
        assertTrue(result);
        verify(taskDAO, times(1)).stopTask(taskId);
    }

    /**
     * Тестируем остановку несуществующей задачи.
     */
    @Test
    @DisplayName("Тестируем остановку несуществующей задачи")
    void testStopTask_TaskNotFound() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 2;

        // Мокируем поведение TaskDAO
        when(taskDAO.stopTask(taskId)).thenThrow(new TaskNotFoundException(taskId));

        // Вызов метода и проверка исключения
        assertFalse(taskService.stopTask(taskId));
        verify(taskDAO, times(1)).stopTask(taskId);
    }

    /**
     * Тестируем завершение существующей задачи.
     */
    @Test
    @DisplayName("Тестируем завершение существующей задачи")
    void testFinishTask_Success() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 1;

        // Мокируем поведение TaskDAO
        when(taskDAO.finishTask(taskId)).thenReturn(true);

        // Вызов метода
        boolean result = taskService.finishTask(taskId);

        // Проверки
        assertTrue(result);
        verify(taskDAO, times(1)).finishTask(taskId);
    }

    /**
     * Тестируем завершение несуществующей задачи.
     */
    @Test
    @DisplayName("Тестируем завершение несуществующей задачи")
    void testFinishTask_TaskNotFound() throws TaskNotFoundException {
        // Подготовка данных
        long taskId = 2;

        // Мокируем поведение TaskDAO
        when(taskDAO.finishTask(taskId)).thenThrow(new TaskNotFoundException(taskId));

        // Вызов метода и проверка исключения
        assertFalse(taskService.finishTask(taskId));
        verify(taskDAO, times(1)).finishTask(taskId);
    }
}
