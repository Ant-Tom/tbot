package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener {

    private final TelegramBot bot;
    private final NotificationTaskRepository taskRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public TelegramBotUpdatesListener(TelegramBot bot, NotificationTaskRepository taskRepository) {
        this.bot = bot;
        this.taskRepository = taskRepository;
    }

    public void process(List<Update> updates) {
        for (Update update : updates) {
            if (update.message().text().equals("/start")) {
                bot.execute(new SendMessage(update.message().chat().id(), "Привет! Отправь напоминание в формате '01.01.2022 20:00 Сделать домашнюю работу'."));
            } else {
                handleNotificationMessage(update);
            }
        }
    }

    private void handleNotificationMessage(Update update) {
        String messageText = update.message().text();
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
        Matcher matcher = pattern.matcher(messageText);

        if (matcher.matches()) {
            String dateTimeString = matcher.group(1);
            String reminderText = matcher.group(3);

            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            NotificationTask task = new NotificationTask();
            task.setChatId(update.message().chat().id());
            task.setMessage(reminderText);
            task.setNotifyAt(dateTime);

            taskRepository.save(task);

            bot.execute(new SendMessage(update.message().chat().id(), "Напоминание сохранено!"));
        } else {
            bot.execute(new SendMessage(update.message().chat().id(), "Неверный формат. Попробуй снова."));
        }
    }
}
