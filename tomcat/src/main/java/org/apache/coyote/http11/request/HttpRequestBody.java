package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> body;

    HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    String getAccount() {
        return body.get("account");
    }

    String getPassword() {
        return body.get("password");
    }

    String getEmail() {
        return body.get("email");
    }
}
