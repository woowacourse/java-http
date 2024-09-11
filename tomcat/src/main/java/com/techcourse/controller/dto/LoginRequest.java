package com.techcourse.controller.dto;

import org.apache.coyote.http11.common.Constants;
import org.apache.coyote.http11.common.FormBodyNameValuePairs;

public record LoginRequest(String account, String password) {

    public static LoginRequest of(String requestBody) {
        FormBodyNameValuePairs nameValuePairs = new FormBodyNameValuePairs(requestBody);
        String account = nameValuePairs.get("account").orElse(Constants.EMPTY_STRING);
        String password = nameValuePairs.get("password").orElse(Constants.EMPTY_STRING);

        return new LoginRequest(account, password);
    }
}
