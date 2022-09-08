package org.apache.coyote.response;

public class ResponseBody {

    private final String content;

    public ResponseBody(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
