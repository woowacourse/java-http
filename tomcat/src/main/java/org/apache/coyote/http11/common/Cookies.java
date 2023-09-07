package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.util.Parser;

public class Cookies {

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

    public Optional<String> getCookie(String name) {
        return Optional.ofNullable(values.get(name));
    }
}
