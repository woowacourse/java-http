package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html;charset=utf-8"),
    JAVA_SCRIPT(".js", "application/js"),
    CSS(".css", "text/css");

    private static final String CONTENT_TYPE_DELIMITER = "\\.";

    private final String contentTypeValue;
    private final String headerValue;

    ContentType(String contentTypeValue, String headerValue) {
        this.contentTypeValue = contentTypeValue;
        this.headerValue = headerValue;
    }

    public static ContentType fromPath(String path) {
        return Arrays.stream(values())
            .filter(it -> path.endsWith(it.contentTypeValue))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    public String getContentTypeValue() {
        return contentTypeValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
