package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseCookie {

    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String COLON_DELIMITER = ": ";
    private static final String EQUALS_SIGN = "=";
    private static final String ATTRIBUTE_DELIMITER = "; ";

    private final Map<String, String> attributes;

    public ResponseCookie() {
        this.attributes = new HashMap<>();
    }

    public void setAttribute(final String name, final String value) {
        attributes.put(name, value);
    }

    public boolean isPresent() {
        return !attributes.isEmpty();
    }

    public String getHeaderString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(SET_COOKIE_HEADER);
        sb.append(COLON_DELIMITER);

        final String attributesString = attributes.entrySet().stream()
                .map(e -> String.join(EQUALS_SIGN, e.getKey(), e.getValue()))
                .collect(Collectors.joining(ATTRIBUTE_DELIMITER));

        sb.append(attributesString);

        return sb.toString();
    }
}
