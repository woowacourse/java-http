package org.apache.coyote.http.response;

public class HttpResponseBody {

    private final String content;

    public HttpResponseBody(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
