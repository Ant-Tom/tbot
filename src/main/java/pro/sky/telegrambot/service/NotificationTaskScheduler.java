package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskScheduler {

    private final NotificationTaskRepository taskRepository;
    private final TelegramBot bot;

    public NotificationTaskScheduler(NotificationTaskRepository taskRepository, TelegramBot bot) {
        this.taskRepository = taskRepository;
        this.bot = bot;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkAndSendNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = taskRepository.findTasksByNotifyAt(now);

        for (NotificationTask task : tasks) {
            bot.execute(new SendMessage(task.getChatId(), task.getMessage()));
            taskRepository.delete(task);
        }
    }
}
