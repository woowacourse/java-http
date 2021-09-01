package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpContentType {
    JS("text/js "),
    SVG("image/svg+xml "),
    CSS("text/css "),
    FAVICON("*/* "),
    NOTHING("text/html;charset=utf-8 ");

    private final String contentTypeResponse;

    HttpContentType(final String contentTypeResponse) {
        this.contentTypeResponse = contentTypeResponse;
    }

    public static HttpContentType matches(final String contentTypeHeader) {
        return Arrays.stream(values())
                .filter(contentType -> contentTypeHeader.toLowerCase().contains(contentType.name().toLowerCase()))
                .findAny()
                .orElse(NOTHING);
    }

    public String getContentTypeResponse() {
        return contentTypeResponse;
    }
}
