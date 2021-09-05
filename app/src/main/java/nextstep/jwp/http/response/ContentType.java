package nextstep.jwp.http.response;

import java.util.Arrays;
import nextstep.jwp.http.request.requestline.RequestPath;

public enum ContentType {
    TEXT("txt", "text/plain"),
    HTML_UTF8("html", "text/html; charset=utf-8"),
    HTML("html", "text/html"),
    ICO("ico", "image/x-icon"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    SVG("svg", "image/svg+xml");

    private final String suffix;
    private final String value;

    ContentType(String suffix, String value) {
        this.suffix = suffix;
        this.value = value;
    }

    public static String of(RequestPath uri) {
        String suffix = uri.getPath().split("\\.")[1];
        return Arrays.stream(ContentType.values())
            .filter(contentType -> contentType.getSuffix().equals(suffix))
            .findAny()
            .orElseThrow(IllegalAccessError::new)
            .value;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getValue() {
        return value;
    }
}
