package org.apache.coyote.http11;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum CharacterSet {
    UTF8(StandardCharsets.UTF_8, "utf-8");

    private final Charset type;
    private final String name;

    CharacterSet(final Charset type, final String name) {
        this.type = type;
        this.name = name;
    }

    public Charset getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
