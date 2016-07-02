package com.girigiri.controller.utils;

/**
 * Created by JianGuo on 7/1/16.
 * POJO for auth in {@link com.girigiri.controller.LoginController}
 */
public class Auth {

    private String name;
    private String password;

    public Auth() {}

    public Auth(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
