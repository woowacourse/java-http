package org.apache.request;

public class HttpBody {

    private final String content;

    public HttpBody(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
