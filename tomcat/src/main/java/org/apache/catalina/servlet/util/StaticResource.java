package org.apache.catalina.servlet.util;

public class StaticResource {

    private final String content;
    private final String contentType;

    public StaticResource(final String content, final String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public String getContentBytes() {
        return new String(content.getBytes());
    }

    public String getContentType() {
        return contentType;
    }
}
