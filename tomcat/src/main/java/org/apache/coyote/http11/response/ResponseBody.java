package org.apache.coyote.http11.response;

public class ResponseBody {
    public static final ResponseBody EMPTY_RESPONSE_BODY = new ResponseBody("");

    private final String content;

    public ResponseBody(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
