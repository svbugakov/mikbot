package org.bot.handlers;

import org.bot.BirthDay;
import org.bot.Group;
import org.bot.Person;
import org.bot.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HappyEventHandler implements HandlerMessage{
    private static final Logger logger = LoggerFactory.getLogger(HappyEventHandler.class);

    @Override
    public String handle(String messageText, long chatId) {
        logger.debug("request b chat_id={}", chatId);

        List<Person> persons = new ArrayList<>();
        for (int date = 1; date <= 31; date++) {
            int monthNumber = LocalDate.now().getMonthValue();
            String key = date + "_" + monthNumber;
            if (BirthDay.getBirthdayMap().containsKey(key)) {
                persons.addAll(BirthDay.getBirthdayMap().get(key)
                        .stream().filter(t -> t.getStatus() == Group.getStatus(chatId) ||
                                chatId == Group.adminGroupID).toList()); // из админской все доступно ??
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
        return response.toString();
    }

    @Override
    public SendPhoto handlePhoto(String messageText) {
        throw new UnsupportedOperationException("not to use handlePhoto for AITextHandler");
    }

    @Override
    public boolean isApply(String messageText) {
        return messageText.equals("/b");
    }
}
