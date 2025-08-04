package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Botmain {

    private static final Logger logger = LoggerFactory.getLogger(Botmain.class);

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new FriendsLip());
            logger.info("Bot is running!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
