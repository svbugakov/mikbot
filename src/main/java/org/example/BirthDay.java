package org.example;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BirthDay {

    private final static Map<String, List<Person>> birthdayMap = new TreeMap<>();

    public static Map<String, List<Person>> getBirthdayMap() {
        return birthdayMap;
    }

    static {
        // Добавляем данные (используем год 2000 как фиктивный)
        // Январь
        // Январь
        addBirthday(birthdayMap, 1, Month.JANUARY, "МАМА", 1966, "family", "birthday");
        addBirthday(birthdayMap, 25, Month.JANUARY, "БАБУШКА Римма", 1941, "family", "birthday");
        addBirthday(birthdayMap, 1, Month.JANUARY, "КАТЯ БАБЕЦ", 2017, "myfriend", "birthday");
        addBirthday(birthdayMap, 31, Month.JANUARY, "КСЮША", 2017, "friend", "birthday");

// Февраль
        addBirthday(birthdayMap, 19, Month.FEBRUARY, "ДЕНИС Лейба", 2016, "myfriend", "birthday");
        addBirthday(birthdayMap, 25, Month.FEBRUARY, "ЛЕОНИД", 2020, "myfriend", "birthday");

// Март
        addBirthday(birthdayMap, 24, Month.MARCH, "Даша", 1991, "admin", "birthday");
        addBirthday(birthdayMap, 24, Month.MARCH, "Ваня", 2017, "admin", "birthday");

// Апрель
        addBirthday(birthdayMap, 1, Month.APRIL, "Оля Бугакова", 1985, "myfriend", "birthday");
        addBirthday(birthdayMap, 9, Month.APRIL, "Паша Пажитных", 1986, "friend", "birthday");
        addBirthday(birthdayMap, 23, Month.APRIL, "Леха Камина", 1991, "myfriend", "birthday");

// Май
        addBirthday(birthdayMap, 7, Month.MAY, "Майя", 1991, "myfriend", "birthday");
        addBirthday(birthdayMap, 20, Month.MAY, "Парвина", 1991, "myfriend", "birthday");

// Июнь
        addBirthday(birthdayMap, 4, Month.JUNE, "Лена", 1992, "myfriend", "birthday");
        addBirthday(birthdayMap, 11, Month.JUNE, "Бабушка Маша", 1938, "family", "birthday");
        addBirthday(birthdayMap, 14, Month.JUNE, "Дядя Андрей", 1960, "family", "birthday");
        addBirthday(birthdayMap, 19, Month.JUNE, "Сережа", 1985, "admin", "birthday");
        addBirthday(birthdayMap, 29, Month.JUNE, "Петр Григорьевич", 1966, "family", "birthday");

// Июль
        addBirthday(birthdayMap, 13, Month.JULY, "Соня", 2019, "admin", "birthday");
        addBirthday(birthdayMap, 15, Month.JULY, "Вик Хрущева", 1984, "myfriend", "birthday");
        addBirthday(birthdayMap, 18, Month.JULY, "Саша Болдырев", 2015, "friend", "birthday");

// Август
        addBirthday(birthdayMap, 2, Month.AUGUST, "Сергей Владимирович и Дарья Юрьевна", 2013, "admin", "year");
        addBirthday(birthdayMap, 2, Month.AUGUST, "Паша и Кристина", 2017, "friend", "year");
        addBirthday(birthdayMap, 7, Month.AUGUST, "Дмитрий Геннадьевич Татьяна Геннадьевна", 2016, "friend", "year");
        addBirthday(birthdayMap, 21, Month.AUGUST, "Валера", 2003, "family", "birthday");
        addBirthday(birthdayMap, 23, Month.AUGUST, "Дядя Юра", 1960, "family", "birthday");
        addBirthday(birthdayMap, 29, Month.AUGUST, "Владимир Алексеевич", 1961, "family", "birthday");
        addBirthday(birthdayMap, 30, Month.AUGUST, "Данила", 1987, "family", "birthday");

// Сентябрь
        addBirthday(birthdayMap, 8, Month.SEPTEMBER, "Надежда Васильевна и Владимир Алексеевич", 1934, "family", "year");
        addBirthday(birthdayMap, 12, Month.SEPTEMBER, "Сергей Палыч", 1985, "myfriend", "birthday");
        addBirthday(birthdayMap, 16, Month.SEPTEMBER, "Ольга Алексеевна и Петр Григорьевич", 1994, "family", "year");
        addBirthday(birthdayMap, 24, Month.SEPTEMBER, "Никита", 2006, "family", "birthday");
        addBirthday(birthdayMap, 24, Month.SEPTEMBER, "Лана Колесникова", 2013, "myfriend", "birthday");
        addBirthday(birthdayMap, 27, Month.SEPTEMBER, "Яна Бабец", 1990, "myfriend", "birthday");

// Октябрь
        addBirthday(birthdayMap, 11, Month.OCTOBER, "Дания Бугаков", 1988, "myfriend", "birthday");
        addBirthday(birthdayMap, 17, Month.OCTOBER, "Миша Шепенев", 1988, "family", "birthday");
        addBirthday(birthdayMap, 17, Month.OCTOBER, "Игорь Бороненцев", 1987, "myfriend", "birthday");
        addBirthday(birthdayMap, 18, Month.OCTOBER, "Юля Иваненко", 1990, "myfriend", "birthday");
        addBirthday(birthdayMap, 20, Month.OCTOBER, "Т. Люда", 1943, "myfriend", "birthday");
        addBirthday(birthdayMap, 21, Month.OCTOBER, "Эммочка", 1995, "family", "birthday");
        addBirthday(birthdayMap, 23, Month.OCTOBER, "Таня Болдырева", 1986, "friend", "birthday");

// Ноябрь
        addBirthday(birthdayMap, 1, Month.NOVEMBER, "Т. Ирина Жирова", 1960, "myfriend", "birthday");
        addBirthday(birthdayMap, 24, Month.NOVEMBER, "Т. Ирина Сулина", 1958, "myfriend", "birthday");
        addBirthday(birthdayMap, 28, Month.NOVEMBER, "Надежда Васильевна", 1958, "family", "birthday");

// Декабрь
        addBirthday(birthdayMap, 3, Month.DECEMBER, "Даня Васильев", 2018, "family", "birthday");
        addBirthday(birthdayMap, 10, Month.DECEMBER, "Дима Болдырев", 1985, "friend", "birthday");
        addBirthday(birthdayMap, 21, Month.DECEMBER, "Галина Васильевна", 1943, "family", "birthday");

    }

    private static void addBirthday(Map<String, List<Person>> map, int day, Month month,
                                    String name, int year, String status, String type) {
        LocalDate date = LocalDate.of(year, month, day); // Год 2000 - фиктивный
        Person person = new Person(name, Status.valueOf(status), Type.valueOf(type), date);
        String key = day + "_" + month.getValue();
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(person);
    }
}
