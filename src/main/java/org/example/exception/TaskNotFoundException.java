package org.example.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое, когда задача с указанным ID не найдена.
 */
public class TaskNotFoundException extends RuntimeException {
    /**
     * Конструктор исключения, принимающий ID задачи.
     *
     * @param taskId ID задачи, которая не была найдена.
     */
    public TaskNotFoundException(long taskId) {
        super(String.format("Нет задачи с таким ID - %s", taskId));
    }
}
