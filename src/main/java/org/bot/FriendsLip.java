package org.bot;

import org.bot.ai.AIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FriendsLip extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(FriendsLip.class);
    private AIManager aiManager;
    private PwdKeeper pwdKeeper;

    public FriendsLip(AIManager aiManager, PwdKeeper pwdKeeper) {
        scheduleWithExecutor();
        this.aiManager = aiManager;
        this.pwdKeeper = pwdKeeper;
    }

    @Override
    public String getBotUsername() {
        return "SvbLibBot"; // Имя бота (без @)
    }

    @Override
    public String getBotToken() {
        return pwdKeeper.getPassword("teleg"); // Токен от @BotFather
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMessage(chatId, "Привет! Я бот %s.\n команды: /start /b /pic.".formatted(Group.getAssistantName(chatId)) +
                        " Для обращения к боту пишем в начале %s ".formatted(Group.getAssistantName(chatId)));
            } else if (messageText.equals("/pic")) {
                // Путь к файлу (подставьте свой)
                String imagePath = "D:\\share\\1.jpg"; // Для Windows
                sendImage(chatId, imagePath);
            } else if (messageText.startsWith("Мика") || messageText.startsWith("Друг")) {
                // Путь к файлу (подставьте свой)
                messageText = messageText.replace("Мика", "");
                messageText = messageText.replace("Друг", "");
                String response;
                try {
                    logger.debug("query: " + messageText);
                    response = aiManager.getResponse(messageText);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                sendMessage(chatId, response);
            } else if (messageText.startsWith("/b")) {
                logger.debug("request b chat_id={}", chatId);

                List<Person> persons = new ArrayList<>();
                for (int date = 1; date <= 31; date++) {
                    int monthNumber = LocalDate.now().getMonthValue();
                    String key = date + "_" + monthNumber;
                    if (BirthDay.getBirthdayMap().containsKey(key)) {
                        persons.addAll(BirthDay.getBirthdayMap().get(key)
                                .stream().filter(t -> t.status == Group.getStatus(chatId) ||
                                        chatId == Group.adminGroupID).toList()); // из админской все доступно ??
                    }
                }
                final StringBuilder response = new StringBuilder();
                response.append("В этом месяце :");
                for (Person p : persons) {
                    if (p.type == Type.birthday) {
                        response.append("\nДень рождения у ").append(p.name).append(" ").append(p.datBorn);
                    } else {
                        response.append("\nГодовщина у ").append(p.name).append(" ").append(p.datBorn);
                    }
                }
                sendMessage(chatId, response.toString());
            } else if (messageText.startsWith("/task")) {
                executeDailyTask();
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendImage(long chatId, String imageUrl) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(String.valueOf(chatId));
        File image = new File(imageUrl);
        photo.setPhoto(new InputFile(image)); // URL или File

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void scheduleWithExecutor() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        LocalTime targetTime = LocalTime.of(7, 10);
        long initialDelay = calculateInitialDelay(targetTime);

        executor.scheduleAtFixedRate(
                this::executeDailyTask,
                initialDelay,
                24 * 60 * 60, // 24 часа в секундах
                TimeUnit.SECONDS
        );

        logger.info("Executor scheduler started. Next execution at {} AM, {}", targetTime, initialDelay);
    }

    // Метод для расчета начальной задержки
    private long calculateInitialDelay(LocalTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(targetTime);

        // Если сегодняшнее время уже прошло 8:00, планируем на завтра
        if (now.toLocalTime().isAfter(targetTime)) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).toSeconds();
    }

    // Метод, который будет выполняться ежедневно
    private void executeDailyTask() {
        logger.info("Executing daily task at: " + LocalDateTime.now());

        // Здесь размещаем логику, которая должна выполняться
        try {

            int monthNumber = LocalDate.now().getMonthValue();
            int dayNumber = LocalDate.now().getDayOfMonth();
            String key = dayNumber + "_" + monthNumber;
            List<Person> persons = new ArrayList<>();
            if (BirthDay.getBirthdayMap().containsKey(key)) {
                persons.addAll(BirthDay.getBirthdayMap().get(key));
            }
            String response;
            if (persons.isEmpty()) {
                String messageReq = "Какой важный праздник или важное событие для русских людей на дату %s ? Отвечай кратко самое важное и значимое.".formatted(LocalDate.now().toString());
                logger.debug("query_taskb: " + messageReq);
                response = aiManager.getResponse(messageReq);
                for (Group group : Group.getGroups()) {
                    sendMessage(group.getChatId(), "Добрый день, сегодня  " + LocalDate.now() + "\n" + response);
                }
            } else {
                for (Person p : persons) {
                    final String desc;
                    if (p.type == Type.birthday) {
                        desc = "днем рождения";
                    } else {
                        desc = "годовщиной";
                    }
                    String messageReq = "Поздравь с %s %s.".formatted(desc, p.name);
                    logger.debug("query_task: " + messageReq);
                    response = aiManager.getResponse(messageReq);
                    if (response.isEmpty()) {
                        response = messageReq + "Желаем здоровья и долгих лет жизни!";
                    }
                    for (Group group : Group.getGroups()) {
                        if (group.getStatus() == p.status || p.status == Status.admin) {
                            sendMessage(group.getChatId(), "Добрый день, сегодня  " + LocalDate.now() + "\n" + response);
                        }
                    }
                    sendMessage(Group.adminGroupID, "Добрый день, сегодня  " + LocalDate.now() + "\n" + response);
                }
            }


        } catch (Exception e) {
            logger.error("Error in daily task: " + e.getMessage());
        }
    }

}
