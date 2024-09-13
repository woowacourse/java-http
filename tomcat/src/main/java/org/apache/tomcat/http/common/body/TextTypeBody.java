package org.apache.tomcat.http.common.body;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var that = (TextTypeBody) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
