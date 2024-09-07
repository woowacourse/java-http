package org.apache.catalina;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript");

    private final String format;
    private final String response;

    ContentType(String format, String response) {
        this.format = format;
        this.response = response;
    }

    public static ContentType findByPath(String path) {
        return Arrays.stream(values())
                .filter(type -> path.endsWith(type.format))
                .findFirst()
                .orElse(HTML);
    }

    public static boolean isStaticFile(String path) {
        return Arrays.stream(values())
                .anyMatch(type -> path.endsWith(type.format));
    }

    public String getResponse() {
        return response + "; charset=utf-8";
    }
}
