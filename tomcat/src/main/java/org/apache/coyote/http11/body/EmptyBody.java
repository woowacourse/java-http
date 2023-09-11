package org.apache.coyote.http11.body;

public class EmptyBody implements Body {
    @Override
    public String getValue(final String key) {
        return "";
    }
}
