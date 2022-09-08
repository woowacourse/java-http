package org.apache.coyote.http11.response;

import java.util.Arrays;

import org.apache.coyote.http11.request.HttpRequest;

enum ContentType {
    CSS("text/css"), HTML("text/html"), JSON("application/json");

    private static final String DEFAULT_CHARSET = "utf-8";
    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    static String from(HttpRequest request) {
        if (!request.containsHeader("Accept")) {
            return addCharSet(HTML, DEFAULT_CHARSET);
        }

        final String accept = request.getHeaderValue("Accept");
        ContentType contentType = Arrays.stream(values())
            .filter(type -> accept.contains(type.value))
            .findFirst()
            .orElse(HTML);
        return addCharSet(contentType, DEFAULT_CHARSET);
    }

    private static String addCharSet(ContentType type, String charset) {
        return type.value + ";charset=" + charset;
    }
}
