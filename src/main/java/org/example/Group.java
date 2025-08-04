package org.example;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public static long adminGroupID = 1042316238l;
    private static final List<Group> groups = new ArrayList<>();

    public static List<Group> getGroups() {
        return groups;
    }

    private final long chatId;
    private final Status status;
    private final String nameAssistant;

    public Group(long chatId, Status status, String name) {
        this.chatId = chatId;
        this.status = status;
        this.nameAssistant = name;
    }

    static {
        groups.add(new Group(-4025066398l, Status.family, "Мика"));
        groups.add(new Group(adminGroupID , Status.admin, "Мика"));
        groups.add(new Group(-1001305919511l , Status.friend, "Друг"));

    }

    public long getChatId() {
        return chatId;
    }

    public Status getStatus() {
        return status;
    }

    public static String getAssistantName(long id) {
        for(final Group g: groups) {
            if(g.chatId == id) {
                return g.nameAssistant;
            }
        }
        return "Друг";
    }

    public static Status getStatus(long id) {
        for(final Group g: groups) {
            if(g.chatId == id) {
                return g.getStatus();
            }
        }
        return Status.myfriend;
    }
}
