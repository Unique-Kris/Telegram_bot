package pro.sky.telegrambot.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.MessageSender;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationTaskJob {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationTaskRepository notificationTaskRepository;

    private final MessageSender messageSender;

    public NotificationTaskJob(NotificationTaskRepository notificationTaskRepository, MessageSender messageSender) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.messageSender = messageSender;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void sendNotification() {

        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        logger.info("Start job for date time {}", currentDateTime);

        List<NotificationTask> notificationTasks =
                notificationTaskRepository.findAllByNotificationDateTime(currentDateTime);

        logger.info("Found {} notification tasks", notificationTasks.size());

        for (NotificationTask notificationTask : notificationTasks) {

            messageSender.send(
                    notificationTask.getChatId(),
                    notificationTask.getMessageText()
            );

            logger.info("Successfully send reminder for task with id {}", notificationTask.getId());
        }
        logger.info("Job is finished");
    }
}
