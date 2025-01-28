package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageSender {

    private Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    public MessageServiceImpl(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    private final TelegramBot telegramBot;

    @Override
    public void send (long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        SendResponse response = telegramBot.execute(message);
        if (response.isOk()) {
            logger.info("Successfully sent message for chatId {} and text {}", chatId, messageText);
        } else {
            logger.warn("Occurred error during sending message chatId {} and text {}", chatId, messageText);
        }
    }
}
