package com.techcourse.model;

import java.util.Arrays;

public enum LoginParam {

    ACCOUNT("account"),
    PASSWORD("password"),
    EMAIL("email");

    private final String param;

    LoginParam(String param) {
        this.param = param;
    }

    public static LoginParam getLoginParam(String param) {
        return Arrays.stream(LoginParam.values())
                .filter(loginParam -> loginParam.param.equals(param))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] no such login param"));
    }
}
