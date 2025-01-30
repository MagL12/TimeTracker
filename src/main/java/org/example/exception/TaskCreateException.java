package org.example.exception;

/**
 * Исключение, выбрасываемое при неудачной попытке создания задачи.
 */
public class TaskCreateException extends RuntimeException {
    /**
     * Конструктор исключения с сообщением о неудачном создании задачи.
     *
     * @param nameTask название задачи, которое не удалось создать
     */
    public TaskCreateException(String nameTask) {
        super(String.format("Не получилось создать задачу - %s", nameTask));
    }
}