package org.apache.coyote.httpresponse;

public class ContentBody {

    private static final String BLANK_CONTENT = "";

    private final String content;

    public ContentBody(final String content) {
        this.content = content;
    }

    public static ContentBody noContent() {
        return new ContentBody(BLANK_CONTENT);
    }

    public String getValue() {
        return content;
    }
}
