package com.zwd.entity;

import org.springframework.stereotype.Component;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-03-21
 **/
@Component
public class User {

    private String name;

    public Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
