package org.apache.coyote.http11.domain.body;

public class ResponseBody {

    private final String body;
    private final ContentType type;

    public ResponseBody(String body, ContentType type) {
        this.body = body;
        this.type = type;
    }

    public String getBody() {
        return body;
    }
}
