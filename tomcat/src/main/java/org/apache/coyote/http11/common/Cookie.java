package org.apache.coyote.http11.common;

import java.util.Map;
import org.apache.coyote.http11.util.Parser;

public class Cookie {

    private Map<String, String> contents;

    public Cookie(final Map<String, String> contents) {
        this.contents = contents;
    }

    public static Cookie create(String line) {
        return Parser.parseToCookie(line);
    }

    public String getCookie(String name) {
        return contents.get(name);
    }
}
