package org.bot;

import java.time.LocalDate;

public class Person {
    final String name;
    final Status status;
    final Type type;
    final LocalDate datBorn;

    public Person(String name, Status status, Type type, LocalDate datBorn) {
        this.name = name;
        this.status = status;
        this.type = type;
        this.datBorn = datBorn;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return type;
    }

    public LocalDate getDatBorn() {
        return datBorn;
    }
}
