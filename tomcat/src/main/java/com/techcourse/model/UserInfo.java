package com.techcourse.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.RequestBody;

public class UserInfo {
    private static final String USER_REGISTRATION_INFO_DELIMITER = "&";
    private static final String INFO_ELEMENT_DELIMITER = "=";
    private static final int ELEMENT_KEY_INDEX = 0;
    private static final int ELEMENT_VALUE_INDEX = 1;


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
        String[] elements = body.split(USER_REGISTRATION_INFO_DELIMITER);

        for (String element : elements) {
            String[] parsedElement = element.split(INFO_ELEMENT_DELIMITER);
            //todo 예외 처리
            registerInfo.put(parsedElement[ELEMENT_KEY_INDEX], parsedElement[ELEMENT_VALUE_INDEX]);
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
