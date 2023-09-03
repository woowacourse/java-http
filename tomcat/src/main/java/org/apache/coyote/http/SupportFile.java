package org.apache.coyote.http;

import java.util.Arrays;
import org.apache.coyote.http.vo.HttpRequest;

public enum SupportFile {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;"),
    JS(".js", "application/javascript;"),
    SVG(".svg", "image/svg+xml;");

    private final String fileExtension;
    private final String contentType;

    SupportFile(final String fileExtension, final String contentType) {
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    public static boolean isSupportFileExtension(final HttpRequest httpRequest) {
        return Arrays.stream(SupportFile.values())
                .anyMatch(it -> httpRequest.isContainsSubStringInUrl(it.fileExtension));
    }

    public static String getContentType(final HttpRequest httpRequest) {
        return Arrays.stream(SupportFile.values())
                .filter(it -> httpRequest.isContainsSubStringInUrl(it.fileExtension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일 확장자 입니다."))
                .contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
