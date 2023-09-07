package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.util.Parser;

public class Cookies {

    private static final String EMPTY_STRING = "";

    private Map<String, String> values;

    public Cookies(final Map<String, String> values) {
        this.values = values;
    }

    public static Cookies create(String line) {
        return Parser.parseToCookie(line);
    }

    public static Cookies empty() {
        return new Cookies(new HashMap<>());
    }

    public String getCookie(String name) {
        return values.getOrDefault(name, EMPTY_STRING);
    }
}
