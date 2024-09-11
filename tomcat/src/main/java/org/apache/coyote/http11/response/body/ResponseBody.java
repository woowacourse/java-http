package org.apache.coyote.http11.response.body;

public class ResponseBody {

    private static final String EMPTY_BODY = "";

    private final String responseBody;

    public ResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public ResponseBody() {
        this.responseBody = EMPTY_BODY;
    }

    public String toMessage() {
        return responseBody;
    }
}
