package org.bot;

import org.bot.ai.AIManager;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.SimpleQuestion;
import org.bot.google.FileManagerGDriver;
import org.bot.handlers.HandlerMessage;
import org.bot.handlers.TypeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FriendsLip<T> extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(FriendsLip.class);
    private AIManager aiManager;
    private PwdKeeper pwdKeeper;
    private FileManagerGDriver driver;
    final List<HandlerMessage<T>> handlerMessageList;

    public FriendsLip(
            AIManager aiManager,
            PwdKeeper pwdKeeper,
            FileManagerGDriver driver,
            final List<HandlerMessage<T>> handlerMessageList
    ) {
        scheduleWithExecutor();
        this.aiManager = aiManager;
        this.pwdKeeper = pwdKeeper;
        this.driver = driver;
        this.handlerMessageList = handlerMessageList;
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
        if (update.hasMessage()) {
            final TypeMessage typeMessage = getTypeMessage(update.getMessage());
            T message = getContent(update.getMessage(), typeMessage);
            long chatId = update.getMessage().getChatId();
            Chat chat = update.getMessage().getChat();
            if(chat.getUserName() == null) {
                chat.setUserName(update.getMessage().getFrom().getUserName());
            }

            for (HandlerMessage<T> handlerMessage : handlerMessageList) {
                if (!handlerMessage.isApplyType(typeMessage) || !handlerMessage.isApply(message)) {
                    continue;
                }
                final ResponseAI result = handlerMessage.handle(
                        message,
                        chat,
                        update.getMessage().getMessageId()
                );

                if (result.getQuestionGoal() == QuestionGoal.TEXT) {
                    sendMessage(chatId, result.getResponse());
                } else if (result.getQuestionGoal() == QuestionGoal.PICTURE) {
                    sendImage(chatId, result.getResponseBytes());
                }
                break;
            }

            if (typeMessage == TypeMessage.TEXT && ((String) message).startsWith("/task")) {
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

    public void sendImage(long chatId, byte[] bytes) {
        SendPhoto photo = new SendPhoto();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputFile inputFile = new InputFile();
        inputFile.setMedia(inputStream, "best photo");// URL или File
        photo.setPhoto(inputFile);
        photo.setChatId(String.valueOf(chatId));
        try {
            execute(photo);
        } catch (Exception e) {
            logger.error("error in send image", e);
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
            String textPresent;
            if (persons.isEmpty()) {
                logger.info("no happy events!");
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
                    SimpleQuestion questionGoal = new SimpleQuestion(messageReq, QuestionGoal.TEXT);
                    ResponseAI response = aiManager.getResponse(questionGoal);
                    if (response.getResponse().isEmpty()) {
                        textPresent = messageReq + "Желаем здоровья и долгих лет жизни!";
                    } else {
                        textPresent = response.getResponse();
                    }
                    for (Group group : Group.getGroups()) {
                        if (group.getStatus() == p.status || p.status == Status.admin) {
                            sendMessage(group.getChatId(), "Добрый день, сегодня  " + LocalDate.now() + "\n" + textPresent);
                        }
                    }
                    sendMessage(Group.adminGroupID, "Добрый день, сегодня  " + LocalDate.now() + "\n" + textPresent);
                }
            }


        } catch (Exception e) {
            logger.error("Error in daily task: " + e.getMessage());
        }
    }

    private TypeMessage getTypeMessage(Message message) {
        if (message.hasPhoto()) {
            return TypeMessage.IMAGE;
        } else if (message.hasVoice()) {
            return TypeMessage.AUDIO;
        } else {
            return TypeMessage.TEXT;
        }
    }

    @SuppressWarnings("unchecked")
    private T getContent(Message message, TypeMessage typeMessage) {
        if (typeMessage == TypeMessage.TEXT) {
            return (T) message.getText();
        } else if (typeMessage == TypeMessage.IMAGE) {
            return (T) message.getPhoto();
        } else {
            return (T) message.getVoice();
        }
    }

}
