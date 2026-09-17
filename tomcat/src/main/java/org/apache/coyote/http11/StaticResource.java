package org.apache.coyote.http11;

public class StaticResource {
    private final String body;
    private final String contentType;

    public StaticResource(String body, String contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }
}
