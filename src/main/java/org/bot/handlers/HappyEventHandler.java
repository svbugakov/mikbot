package org.bot.handlers;

import org.bot.BirthDay;
import org.bot.Group;
import org.bot.Person;
import org.bot.Type;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HappyEventHandler implements HandlerMessage<String>{
    private static final Logger logger = LoggerFactory.getLogger(HappyEventHandler.class);

    @Override
    public ResponseAI handle(String messageText, Chat chat, int messageId) {
        logger.debug("request b chat_id={}", chat.getId());

        List<Person> persons = new ArrayList<>();
        for (int date = 1; date <= 31; date++) {
            int monthNumber = LocalDate.now().getMonthValue();
            String key = date + "_" + monthNumber;
            if (BirthDay.getBirthdayMap().containsKey(key)) {
                persons.addAll(BirthDay.getBirthdayMap().get(key)
                        .stream().filter(t -> t.getStatus() == Group.getStatus(chat.getId()) ||
                                chat.getId() == Group.adminGroupID).toList()); // из админской все доступно ??
            }
        }
        final StringBuilder response = new StringBuilder();
        response.append("В этом месяце :");
        for (Person p : persons) {
            if (p.getType() == Type.birthday) {
                response.append("\nДень рождения у ").append(p.getName()).append(" ").append(p.getDatBorn());
            } else {
                response.append("\nГодовщина у ").append(p.getName()).append(" ").append(p.getDatBorn());
            }
        }
        return new ResponseAI(response.toString(), StatusResponse.SUCCESS, QuestionGoal.TEXT);
    }

    @Override
    public boolean isApply(String messageText) {
        return messageText.equals("/b");
    }
}
