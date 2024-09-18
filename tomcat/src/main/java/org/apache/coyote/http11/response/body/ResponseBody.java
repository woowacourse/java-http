package org.apache.coyote.http11.response.body;

import static org.apache.coyote.http11.Constants.EMPTY;

import java.nio.charset.StandardCharsets;

public class ResponseBody {

    private final String responseBody;

    public ResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public ResponseBody() {
        this.responseBody = EMPTY;
    }

    public String getContentLength() {
        return Integer.toString(responseBody.getBytes(StandardCharsets.UTF_8).length);
    }

    public String toMessage() {
        return responseBody;
    }
}
