package org.rabbit.utils;

import org.rabbit.module.People;

import java.util.Arrays;
import java.util.List;

public class ModuleHelper {

    public static List<People> getListPeople() {
        People people = new People();
        people.setName("lily");
        people.setAge(20);
        people.setSex("女");
        people.setScore(88.3);

        People people1 = new People();
        people1.setName("Tom");
        people1.setAge(21);
        people1.setSex("男");
        people1.setScore(78.3);


        List<People> list = Arrays.asList(people, people1);

        return list;
    }
}
