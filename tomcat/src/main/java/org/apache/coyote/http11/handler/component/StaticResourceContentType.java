package org.apache.coyote.http11.handler.component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public enum StaticResourceContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "text/javascript");

    private final String extension;
    private final String value;

    StaticResourceContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static boolean findable(final String targetUrl) {
        return Arrays.stream(values())
            .anyMatch(contentType -> targetUrl.endsWith(contentType.extension));
    }

    public static String find(final String targetUrl) {
        final StaticResourceContentType findType = Arrays.stream(values())
            .filter(contentType -> targetUrl.endsWith(contentType.extension))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "요청한 리소스의 확장자와 일치하는 Content-Type 이 없습니다. 요청한 리소스 :" + targetUrl)
            );
        if (findType == HTML) {
            return findType.value + ";charset=" + StandardCharsets.UTF_8;
        }
        return findType.value;
    }

    public static String findWithAdditionalCharset(final String targetUrl, final Charset charset) {
        final StaticResourceContentType findType = Arrays.stream(values())
            .filter(contentType -> targetUrl.endsWith(contentType.extension))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "요청한 리소스의 확장자와 일치하는 Content-Type 이 없습니다. 요청한 리소스 :" + targetUrl)
            );

        return findType.value + findType + ";charset=" + charset;
    }
}
