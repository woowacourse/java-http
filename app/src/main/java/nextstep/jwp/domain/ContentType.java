package nextstep.jwp.domain;

import java.util.Arrays;

public enum ContentType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    IMAGE(".svg", "image/svg+xml");

    private String suffix;
    private String contentType;

    ContentType(String suffix, String contentType) {
        this.suffix = suffix;
        this.contentType = contentType;
    }

    public static String findContentType(String input) {
        return Arrays.stream(values())
                .filter(value -> input.endsWith(value.suffix))
                .map(value -> value.contentType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 content type이 없습니다."));
    }
}

