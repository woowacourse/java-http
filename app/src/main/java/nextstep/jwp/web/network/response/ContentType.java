package nextstep.jwp.web.network.response;

import nextstep.jwp.web.exception.InvalidHttpContentTypeException;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    JAVASCRIPT("js", "application/x-javascript"),
    IMAGE("ico", "image/x-icon");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType find(String extension) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.extension.equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(InvalidHttpContentTypeException::new);
    }

    public String getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }

    public boolean is(String path) {
        return this.name().equalsIgnoreCase(path);
    }
}
