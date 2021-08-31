package nextstep.jwp.util;

import java.net.URL;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    SVG("svg", "image/svg+xml"),
    ICO("ico", "image/x-icon");

    private final String suffix;
    private final String value;

    ContentType(String suffix, String value) {
        this.suffix = suffix;
        this.value = value;
    }

    public static ContentType findType(URL resourceUrl) {
        String uri = resourceUrl.toString();
        for (ContentType contentType : ContentType.values()) {
            if (uri.endsWith(contentType.suffix)) {
                return contentType;
            }
        }
        return ContentType.HTML;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getValue() {
        return value;
    }
}
