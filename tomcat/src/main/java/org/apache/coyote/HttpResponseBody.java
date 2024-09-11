package org.apache.coyote;

public class HttpResponseBody {

    private String content;

    public HttpResponseBody() {
        this.content = "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
