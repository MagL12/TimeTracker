package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.DatabaseConnection;
import org.example.entity.Task;
import org.example.exception.TaskNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Класс TaskDAO отвечает за взаимодействие с базой данных для выполнения операций с задачами.
 * Он предоставляет методы для добавления, обновления, удаления и получения задач из базы данных.
 */
public class TaskDAO {

    private static final Logger logger = LogManager.getLogger(TaskDAO.class);

    /**
     * Конструктор по умолчанию.
     */
    public TaskDAO() {
    }

    /**
     * Проверяет, существует ли задача с указанным идентификатором в базе данных.
     *
     * @param taskId Идентификатор задачи.
     * @return true, если задача существует, иначе false.
     */
    private boolean checkTaskExists(UUID taskId) {
        String sqlSelect = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setObject(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Добавляет новую задачу в базу данных.
     *
     * @param task Задача, которую нужно добавить.
     * @return Optional, содержащий UUID добавленной задачи, если операция прошла успешно, иначе пустой Optional.
     */
    public Optional<UUID> addTask(Task task) {
        String sql = "INSERT INTO tasks (name, start_time, stop_time, status) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getName());
            pstmt.setTimestamp(2, Timestamp.valueOf(task.getStartTime()));
            pstmt.setTimestamp(3, task.getStopTime() != null ? java.sql.Timestamp.valueOf(task.getStopTime()) : null);
            pstmt.setString(4, task.getStatus());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UUID taskId = rs.getObject("id", UUID.class);
                    logger.info("Task added with ID: {}", taskId);
                    return Optional.ofNullable(taskId);
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding task", e);
        }
        return Optional.empty();
    }

    /**
     * Получает список всех задач из базы данных.
     *
     * @return Список задач.
     */
    public List<Task> getAllTasks() {
        String sql = "SELECT * FROM tasks";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                String name = rs.getString("name");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime stopTime = rs.getTimestamp("stop_time") != null ? rs.getTimestamp("stop_time").toLocalDateTime() : null;
                String status = rs.getString("status");

                Task task = new Task();
                task.setId(id);
                task.setName(name);
                task.setStartTime(startTime);
                task.setStopTime(stopTime);
                task.setStatus(status);

                tasks.add(task);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving tasks", e);
        }
        return tasks;
    }

    /**
     * Обновляет имя задачи в базе данных.
     *
     * @param taskId  Идентификатор задачи.
     * @param newName Новое имя задачи.
     * @return true, если обновление прошло успешно, иначе false.
     * @throws TaskNotFoundException Если задача с указанным идентификатором не найдена.
     */
    public boolean updateTaskName(UUID taskId, String newName) {
        if (!checkTaskExists(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        String sql = "UPDATE tasks SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setObject(2, taskId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Task name updated for ID: {}", taskId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating task name for ID: {}", taskId, e);
        }
        return false;
    }

    /**
     * Удаляет задачу из базы данных.
     *
     * @param taskId Идентификатор задачи.
     * @return true, если удаление прошло успешно, иначе false.
     * @throws TaskNotFoundException Если задача с указанным идентификатором не найдена.
     */
    public boolean deleteTask(UUID taskId) {
        if (!checkTaskExists(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, taskId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Task deleted with ID: {}", taskId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting task with ID: {}", taskId, e);
        }
        return false;
    }

    /**
     * Останавливает задачу, обновляя её статус на "Остановлена" и устанавливая время остановки.
     *
     * @param taskId Идентификатор задачи.
     * @return true, если операция прошла успешно, иначе false.
     * @throws TaskNotFoundException Если задача с указанным идентификатором не найдена.
     */
    public boolean stopTask(UUID taskId) {
        if (!checkTaskExists(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        String sql = "UPDATE tasks SET stop_time = ?, status = 'Остановлена' WHERE id = ? AND status = 'Активна'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setObject(2, taskId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Task stopped with ID: {}", taskId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error stopping task with ID: {}", taskId, e);
        }
        return false;
    }

    /**
     * Завершает задачу, обновляя её статус на "Завершена".
     *
     * @param taskId Идентификатор задачи.
     * @return true, если операция прошла успешно, иначе false.
     * @throws TaskNotFoundException Если задача с указанным идентификатором не найдена.
     */
    public boolean finishTask(UUID taskId) {
        if (!checkTaskExists(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        String sql = "UPDATE tasks SET status = 'Завершена' WHERE id = ? AND status IN ('Активна', 'Остановлена')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, taskId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Task finished with ID: {}", taskId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error finishing task with ID: {}", taskId, e);
        }
        return false;
    }
}