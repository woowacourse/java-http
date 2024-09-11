package org.apache.coyote.http;

public class HttpBody implements HttpComponent {

    private final String content;
    private final int length;

    public HttpBody(final String content) {
        this.content = content;
        this.length = content.getBytes().length;
    }

    public String getContent() {
        return content;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String asString() {
        return getContent();
    }
}
