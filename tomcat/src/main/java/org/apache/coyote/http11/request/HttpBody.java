package org.apache.coyote.http11.request;

public class HttpBody {
    private final String content;

    public HttpBody(String content) {
        this.content = content;
    }

    public static HttpBody empty() {
        return new HttpBody("");
    }

    public String getContent() {
        return content;
    }
}
