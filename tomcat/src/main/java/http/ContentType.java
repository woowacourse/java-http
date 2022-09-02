package http;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ;

    private final String extension;
    private final String contentType;

    ContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType findContentType(final String extension) {
        return Arrays.stream(values())
                .filter(it -> it.extension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("확장자가 적절하지 않습니다."));
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}
