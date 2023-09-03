package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpException;
import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    X_ICON(".ico", "image/x-icon");

    private final String extension;
    private final String response;

    ContentType(final String extension, final String response) {
        this.extension = extension;
        this.response = response;
    }

    public static ContentType findByPath(final String path) {
        return Arrays.stream(values())
                .filter(it -> path.endsWith(it.extension))
                .findFirst()
                .orElseThrow(() -> new HttpException("지원하지 않는 확장자입니다."));
    }

    public String getResponse() {
        return response;
    }
}
