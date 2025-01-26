package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.MessageSender;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final MessageSender messageSender;
    private TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;

    private final String WELCOME_MESSAGE = "Привет, друг!";
    private final String WRONG_FORMAT_MESSAGE = "Неверный формат сообщения";
    private final String NEW_NOTIFICATION_SAVED_MESSAGE = "Спасибо, напоминание добавлено!";
    private final Pattern INCOMING_MESSAGE = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public TelegramBotUpdatesListener(MessageSender messageSender, TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.messageSender = messageSender;
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            long chatId = update.message().chat().id();
            String message = update.message().text();
            if ("/start".equals(message)) {
                messageSender.send(chatId, WELCOME_MESSAGE);
            } else {
                Matcher matcher = INCOMING_MESSAGE.matcher(message);
                if (matcher.matches()) {

                    LocalDateTime notificationDateTime = LocalDateTime.parse(matcher.group(1), DATE_FORMAT);
                    String notificationMessage = matcher.group(3);

                    NotificationTask notificationTask = new NotificationTask();

                    notificationTask.setChatId(chatId);
                    notificationTask.setMessageText(notificationMessage);
                    notificationTask.setNotificationDateTime(notificationDateTime);

                    notificationTaskRepository.save(notificationTask);

                    messageSender.send(chatId, NEW_NOTIFICATION_SAVED_MESSAGE);
                    logger.info("Successfully saved new notification with id {}", notificationTask.getId());

                } else {
                    messageSender.send(chatId, WRONG_FORMAT_MESSAGE);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
