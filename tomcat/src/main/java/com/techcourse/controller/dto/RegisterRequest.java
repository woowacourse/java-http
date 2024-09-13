package com.techcourse.controller.dto;

import org.apache.coyote.http11.common.Constants;
import org.apache.coyote.http11.common.FormBodyNameValuePairs;

public record RegisterRequest(String account, String email, String password) {

    public static RegisterRequest of(String requestBody) {
        FormBodyNameValuePairs nameValuePairs = new FormBodyNameValuePairs(requestBody);
        String account = nameValuePairs.get("account").orElse(Constants.EMPTY_STRING);
        String password = nameValuePairs.get("password").orElse(Constants.EMPTY_STRING);
        String email = nameValuePairs.get("email").orElse(Constants.EMPTY_STRING);

        return new RegisterRequest(account, email, password);
    }
}
