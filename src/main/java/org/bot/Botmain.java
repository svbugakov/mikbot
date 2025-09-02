package org.bot;

import org.bot.ai.AIManager;
import org.bot.gdrive.FileManagerGDriver;
import org.bot.handlers.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Botmain {

    private static final Logger logger = LoggerFactory.getLogger(Botmain.class);

    public static void main(String[] args) throws Exception {
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            PwdKeeper pwdKeeper = new PwdKeeper();
            SslContextKeeper sslContextKeeper = new SslContextKeeper();
            SslContextKeeper.SslConfig sslConfig = new SslContextKeeper.SslConfig(
                    "client_new_key.jks",
                    pwdKeeper.getPassword("pwdStore"),
                    "client_new_ca.jks",
                    pwdKeeper.getPassword("pwdStore")
            );
            sslContextKeeper.addSslContext("stt", sslConfig);

            AIManager aiManager = new AIManager(pwdKeeper, sslContextKeeper);
            FileManagerGDriver driver = new FileManagerGDriver("gdrive.json");


            final List<HandlerMessage> handlerMessageList = new ArrayList<>();
            handlerMessageList.add(new AITextHandler(aiManager));
            handlerMessageList.add(new TextToImageHandler(driver));
            handlerMessageList.add(new StartHandler());
            handlerMessageList.add(new HappyEventHandler());
            final FriendsLip friendsLip = new FriendsLip(aiManager, pwdKeeper, driver, handlerMessageList);
            handlerMessageList.add(new AudioToTextHandler(
                            aiManager,
                            pwdKeeper.getPassword("teleg"),
                            friendsLip
                    )
            );
            botsApi.registerBot(friendsLip);
            logger.info("BotMik is running...");
        } catch (Exception e) {
            logger.error("fatal error:", e);
            throw e;
        }
    }
}
