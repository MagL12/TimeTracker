package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс Task представляет сущность задачи.
 * Он содержит информацию о задаче, такую как идентификатор, имя, время начала, время остановки и статус.
 */
@Getter
@Setter
public class Task {
    private UUID id; // Уникальный идентификатор задачи
    private String name; // Название задачи
    private LocalDateTime startTime; // Время начала задачи
    private LocalDateTime stopTime; // Время остановки задачи
    private String status; // Статус задачи (например, "Активна", "Остановлена", "Завершена")

    /**
     * Вычисляет продолжительность выполнения задачи.
     * Если задача активна, возвращает продолжительность с момента начала до текущего времени.
     * Если задача остановлена или завершена, возвращает продолжительность между временем начала и временем остановки.
     *
     * @return Продолжительность выполнения задачи.
     */
    public Duration getDuration() {
        if ("Активна".equals(status)) {
            // Если задача активна, вычисляем продолжительность с момента начала до текущего времени
            Duration totalDuration = Duration.between(startTime, LocalDateTime.now());
            if (stopTime != null) {
                // Если задача была остановлена, вычитаем время простоя
                totalDuration = totalDuration.minus(Duration.between(stopTime, LocalDateTime.now()));
            }
            return totalDuration;
        }

        // Если задача не активна, вычисляем продолжительность между временем начала и временем остановки
        Duration totalDuration = Duration.between(startTime, stopTime);
        if (stopTime != null) {
            // Корректируем продолжительность, если задача была остановлена
            totalDuration = totalDuration.minus(Duration.between(stopTime, stopTime));
        }
        return totalDuration;
    }

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