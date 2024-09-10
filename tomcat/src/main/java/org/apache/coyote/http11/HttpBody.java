package org.apache.coyote.http11;

public class HttpBody {

    private final String content;

    public HttpBody(String content) {
        if (content == null) {
            this.content = "";
            return;
        }
        this.content = content;
    }

    public HttpBody(byte[] content) {
        this(new String(content));
    }

    public static HttpBody empty() {
        return new HttpBody("");
    }

    public long getContentLength() {
        return content.getBytes().length;
    }

    public String getContent() {
        return content;
    }
}
