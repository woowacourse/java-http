package org.apache.coyote.http11.response;

public class ResponseBody {

    private final String content;

    public ResponseBody(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
