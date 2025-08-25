package org.bot;

import org.bot.ai.AIManager;
import org.bot.gdrive.FileManagerGDriver;
import org.bot.handlers.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Botmain {

    private static final Logger logger = LoggerFactory.getLogger(Botmain.class);

    public static void main(String[] args) throws Exception {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            PwdKeeper pwdKeeper = new PwdKeeper();
            AIManager aiManager = new AIManager(pwdKeeper);
            FileManagerGDriver driver = new FileManagerGDriver("gdrive.json");

            final List<HandlerMessage> handlerMessageList = new ArrayList<>();
            handlerMessageList.add(new AITextHandler(aiManager));
            handlerMessageList.add(new ImageHandler(driver));
            handlerMessageList.add(new StartHandler());
            handlerMessageList.add(new HappyEventHandler());

            botsApi.registerBot(new FriendsLip(aiManager, pwdKeeper, driver, handlerMessageList));
            logger.info("BotMik is running...");
        } catch (Exception e) {
            logger.error("fatal error:", e);
            throw e;
        }
    }
}
