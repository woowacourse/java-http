package nextstep.jwp.server.http.common;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    IMAGE(".svg", "image/svg+xml");

    private final String suffix;
    private final String type;

    ContentType(String suffix, String type) {
        this.suffix = suffix;
        this.type = type;
    }

    public static String findContentType(String input) {
        return Arrays.stream(values())
                .filter(value -> input.endsWith(value.suffix))
                .map(value -> value.type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 content type 입니다."));
    }
}
