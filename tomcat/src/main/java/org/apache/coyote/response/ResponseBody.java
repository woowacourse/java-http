package org.apache.coyote.response;

public class ResponseBody {

    private final String body;
    private final ContentType type;

    public ResponseBody(final String body, final ContentType type) {
        this.body = body;
        this.type = type;
    }

    public String getBody() {
        return body;
    }
}
