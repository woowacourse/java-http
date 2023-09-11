package nextstep.jwp.common;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("Application/javascript", "js");

    private final String contentType;
    private final String name;

    ContentType(String contentType, String name) {
        this.contentType = contentType;
        this.name = name;
    }

    public static ContentType from(String url) {
        return Arrays.stream(values())
                .filter(type -> url.contains(type.name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 타입이 없습니다"));
    }

    public String getContentType() {
        return contentType;
    }
}
