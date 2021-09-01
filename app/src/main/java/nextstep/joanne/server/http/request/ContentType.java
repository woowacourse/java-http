package nextstep.joanne.server.http.request;

import nextstep.joanne.dashboard.exception.PageNotFoundException;

import java.util.stream.Stream;

public enum ContentType {
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "application/javascript"),
    HTML(".html", "text/html"),
    ;

    private final String extensions;
    private final String contentType;

    ContentType(String extensions, String contentType) {
        this.extensions = extensions;
        this.contentType = contentType;
    }

    public static String resolve(String url) {
        if (!url.contains(".")) {
            return HTML.contentType();
        }
        return Stream.of(values())
                .filter(value -> url.endsWith(value.extensions))
                .findAny()
                .orElseThrow(PageNotFoundException::new)
                .contentType;
    }

    public String contentType() {
        return contentType;
    }

    public String extension() {
        return extensions;
    }
}
