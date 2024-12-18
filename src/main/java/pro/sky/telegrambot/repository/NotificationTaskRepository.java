package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    @Query("SELECT t FROM NotificationTask t WHERE t.notifyAt = :time")
    List<NotificationTask> findTasksByNotifyAt(LocalDateTime time);
}
