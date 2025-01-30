package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.TaskDAO;
import org.example.entity.Task;
import org.example.exception.TaskNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Сервис для управления задачами. Обеспечивает выполнение операций над задачами,
 * таких как добавление, обновление, удаление и завершение задач.
 * Использует пул блокировок для обеспечения потокобезопасности.
 */
public class TaskService {

    private TaskDAO taskDAO;
    private static final Logger logger = LogManager.getLogger(TaskService.class);

    private static final Lock[] lockPool = new ReentrantLock[16]; // Фиксированное количество блокировок

    static {
        for (int i = 0; i < lockPool.length; i++) {
            lockPool[i] = new ReentrantLock();
        }
    }

    /**
     * Возвращает блокировку для задачи на основе её UUID.
     *
     * @param taskId UUID задачи
     * @return блокировка, связанная с задачей
     */
    private Lock getLock(Long taskId) {
        int index = Math.abs(taskId.hashCode()) % lockPool.length;
        return lockPool[index];
    }

    /**
     * Конструктор для создания экземпляра TaskService.
     *
     * @param taskDAO DAO для работы с задачами
     */
    public TaskService(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
        logger.info("TaskService initialized");
    }

    /**
     * Добавляет новую задачу с указанным именем.
     *
     * @param name имя задачи
     * @return Optional с UUID добавленной задачи, если операция успешна, иначе пустой Optional
     */
    public Optional<Long> addTask(String name) {
        Task task = new Task();
        task.setName(name);
        task.setStartTime(LocalDateTime.now());
        task.setStatus("Активна");

        return taskDAO.addTask(task);
    }

    /**
     * Возвращает список всех задач.
     *
     * @return список задач
     */
    public List<Task> getAllTasks() {
        return taskDAO.getAllTasks();
    }

    /**
     * Обновляет имя задачи по её UUID.
     *
     * @param taskId  UUID задачи
     * @param newName новое имя задачи
     * @return true, если операция успешна, иначе false
     */
    public boolean updateTaskName(long taskId, String newName) {
        Lock lock = getLock(taskId);
        lock.lock();
        try {
            boolean result = taskDAO.updateTaskName(taskId, newName);
            if (result) {
                logger.info("Task name updated to {} for ID: {}", newName, taskId);
            } else {
                logger.warn("Failed to update task name for ID: {}", taskId);
            }
            return result;
        } catch (TaskNotFoundException e) {
            logger.warn(e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет задачу по её UUID.
     *
     * @param taskId UUID задачи
     * @return true, если операция успешна, иначе false
     */
    public boolean deleteTask(long taskId) {
        Lock lock = getLock(taskId);
        lock.lock();
        try {
            boolean result = taskDAO.deleteTask(taskId);
            if (result) {
                logger.info("Task deleted with ID: {}", taskId);
            } else {
                logger.warn("Failed to delete task with ID: {}", taskId);
            }
            return result;
        } catch (TaskNotFoundException e) {
            logger.warn(e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Останавливает задачу по её UUID.
     *
     * @param taskId UUID задачи
     * @return true, если операция успешна, иначе false
     */
    public boolean stopTask(long taskId) {
        Lock lock = getLock(taskId);
        lock.lock();
        try {
            boolean result = taskDAO.stopTask(taskId);
            if (result) {
                logger.info("Task stopped with ID: {}", taskId);
            } else {
                logger.warn("Failed to stop task with ID: {}", taskId);
            }
            return result;
        } catch (TaskNotFoundException e) {
            logger.warn(e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Завершает задачу по её UUID.
     *
     * @param taskId UUID задачи
     * @return true, если операция успешна, иначе false
     */
    public boolean finishTask(long taskId) {
        Lock lock = getLock(taskId);
        lock.lock();
        try {
            boolean result = taskDAO.finishTask(taskId);
            if (result) {
                logger.info("Task finished with ID: {}", taskId);
            } else {
                logger.warn("Failed to finish task with ID: {}", taskId);
            }
            return result;
        } catch (TaskNotFoundException e) {
            logger.warn(e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Вычисляет продолжительность выполнения задачи.
     * Если задача активна, возвращает продолжительность с момента начала до текущего времени.
     * Если задача остановлена или завершена, возвращает продолжительность между временем начала и временем остановки.
     *
     * @return Продолжительность выполнения задачи.
     */
    public Duration getDuration(Task task) {
        if ("Активна".equals(task.getStatus())) {
            // Если задача активна, вычисляем продолжительность с момента начала до текущего времени
            Duration totalDuration = Duration.between(task.getStartTime(), LocalDateTime.now());
            if (task.getStopTime() != null) {
                // Если задача была остановлена, вычитаем время простоя
                totalDuration = totalDuration.minus(Duration.between(task.getStopTime(), LocalDateTime.now()));
            }
            return totalDuration;
        }

        // Если задача не активна, вычисляем продолжительность между временем начала и временем остановки
        Duration totalDuration = Duration.between(task.getStartTime(), task.getStopTime());
        if (task.getStopTime() != null) {
            // Корректируем продолжительность, если задача была остановлена
            totalDuration = totalDuration.minus(Duration.between(task.getStartTime(), task.getStartTime()));
        }
        return totalDuration;
    }
}