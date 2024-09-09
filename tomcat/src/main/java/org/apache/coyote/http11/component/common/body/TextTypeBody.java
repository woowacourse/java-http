package org.apache.coyote.http11.component.common.body;

public class TextTypeBody implements Body<String> {

    private final String content;

    public TextTypeBody(final String content) {
        this.content = content;
    }

    @Override
    public String serialize() {
        return content;
    }

    @Override
    public String deserialize() {
        return content;
    }
}
