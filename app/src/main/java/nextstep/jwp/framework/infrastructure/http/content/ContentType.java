package nextstep.jwp.framework.infrastructure.http.content;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript");

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType find(String resourceFilePath) {
        return Arrays.stream(ContentType.values())
            .filter(t -> resourceFilePath.endsWith(t.extension))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("유효하지 않은 파일 확장자"));
    }

    public String getContentType() {
        return contentType;
    }
}
