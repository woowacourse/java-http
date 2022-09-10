package jakarta.http;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "text/js;charset=utf-8");

    private String extension;
    private String contentType;

    ContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType findContentType(final String otherUrl) {
        return Arrays.stream(values())
                .filter(value -> otherUrl.contains(value.extension))
                .findAny()
                .orElse(HTML);
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}
