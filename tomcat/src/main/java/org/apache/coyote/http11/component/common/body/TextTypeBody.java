package org.apache.coyote.http11.component.common.body;

public class TextTypeBody implements Body {

    private final String content;

    public TextTypeBody(final String content) {
        this.content = content;
    }

    @Override
    public String getContent(final String key) {
        return "";
    }

    @Override
    public String deserialize() {
        return content;
    }
}
