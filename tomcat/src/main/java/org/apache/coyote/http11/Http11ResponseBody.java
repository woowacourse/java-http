package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class Http11ResponseBody {

    private final String body;

    private Http11ResponseBody(String body) {
        this.body = body;
    }

    public static Http11ResponseBody of(String response) {
        return new Http11ResponseBody(response);
    }

    public int getContentLength() {
        return body.getBytes(StandardCharsets.UTF_8).length;
    }

    public String getBody() {
        return body;
    }
}
