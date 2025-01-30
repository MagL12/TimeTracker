package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс Task представляет сущность задачи.
 * Он содержит информацию о задаче, такую как идентификатор, имя, время начала, время остановки и статус.
 */
@Getter
@Setter
public class Task {
    private long id; // Уникальный идентификатор задачи
    private String name; // Название задачи
    private LocalDateTime startTime; // Время начала задачи
    private LocalDateTime stopTime; // Время остановки задачи
    private String status; // Статус задачи (например, "Активна", "Остановлена", "Завершена")


    /**
     * Возвращает строковое представление задачи.
     *
     * @return Строка, содержащая информацию о задаче.
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", status='" + status + '\'' +
                '}';
    }
}