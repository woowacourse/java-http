package com.techcourse.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.RequestBody;

public class UserInfo {
    private final String account;
    private final String password;
    private final String email;

    public UserInfo(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static UserInfo read(RequestBody requestBody) {
        Map<String, String> registerInfo = new HashMap<>();
        String body = requestBody.getContent();
        String[] elements = body.split("&");

        for (String element : elements) {
            String[] parsedElement = element.split("=");
            registerInfo.put(parsedElement[0], parsedElement[1]);
        }

        return new UserInfo(
                registerInfo.get("account"),
                registerInfo.get("password"),
                registerInfo.get("email")
        );
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
