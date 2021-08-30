package nextstep.joanne.http.request;

import nextstep.joanne.exception.PageNotFoundException;

import java.util.Arrays;

public enum ContentType {
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "application/javascript"),
    HTML(".html", "text/html"),
    ;

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String resolve(String url) {
        return Arrays.stream(values())
                .filter(content -> url.endsWith(content.extension))
                .findAny()
                .orElseThrow(PageNotFoundException::new)
                .contentType;
    }

    public String contentType() {
        return contentType;
    }

    public String extension() {
        return extension;
    }
}
