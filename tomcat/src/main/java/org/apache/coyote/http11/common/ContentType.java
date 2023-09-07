package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum ContentType {
    TEXT_PLAIN("", "text/plain;charset=utf-8"),
    TEXT_HTML(".html", "text/html;charset=utf-8"),
    TEXT_CSS(".css", "text/css"),
    APPLICATION_JAVASCRIPT(".js", "application/javascript");

    private String extension;
    private String detail;

    ContentType(final String extension, final String detail) {
        this.extension = extension;
        this.detail = detail;
    }

    public String getExtension() {
        return extension;
    }

    public String getDetail() {
        return detail;
    }

    public static String getDetailfromExtension(String extension) {
        return Arrays.stream(ContentType.values())
                .filter(it -> it.extension.equals(extension))
                .findFirst()
                .map(ContentType::getDetail)
                .orElseThrow(() -> new IllegalArgumentException("No enum constant with extension: " + extension));
    }
}
