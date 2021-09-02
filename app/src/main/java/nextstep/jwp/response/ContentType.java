package nextstep.jwp.response;

import java.util.Arrays;

public enum ContentType {
    JS(".js", "text/javascript"),
    CSS(".css", "text/css"),
    SVG(".svg", "image/svg+xml"),
    HTML(".html", "text/html;charset=utf-8");

    private final String endPath;
    private final String mime;

    ContentType(String endPath, String mime) {
        this.endPath = endPath;
        this.mime = mime;
    }

    public static String findContentType(String url) {
        return Arrays.stream(ContentType.values())
                     .filter(contentType -> url.endsWith(contentType.endPath))
                     .findFirst()
                     .orElse(HTML).mime;
    }
}
