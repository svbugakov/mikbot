package org.bot;

import org.bot.ai.AIManager;
import org.bot.gdrive.FileManagerGDriver;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Botmain {

    private static final Logger logger = LoggerFactory.getLogger(Botmain.class);

    public static void main(String[] args) throws TelegramApiException, IOException, GeneralSecurityException {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            PwdKeeper pwdKeeper = new PwdKeeper();
            AIManager aiManager = new AIManager(pwdKeeper);
            FileManagerGDriver driver = new FileManagerGDriver("gdrive.json");
            botsApi.registerBot(new FriendsLip(aiManager, pwdKeeper, driver));
            logger.info("BotMik is running...");
        } catch (Exception e) {
            logger.error("fatal error:", e);
            throw e;
        }
    }
}
