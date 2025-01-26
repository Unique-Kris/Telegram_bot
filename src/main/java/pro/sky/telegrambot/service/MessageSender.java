package pro.sky.telegrambot.service;

public interface MessageSender {
    public void send (long chatId, String messageText);
}
