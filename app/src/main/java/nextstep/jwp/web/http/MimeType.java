package nextstep.jwp.web.http;

import java.util.Arrays;
import java.util.List;

public enum MimeType {
    TEXT_HTML("text/html"),
    JSON("application/json"),
    TEXT_PLAIN("text/plain"),
    TEXT_CSS("text/css"),
    TEXT_JS("text/js"),
    TEXT_ALL("text/*"),
    ALL("*/*");

    private final String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static MimeType findByName(List<String> mimeTypes) {
        return Arrays.stream(values())
            .filter(m -> mimeTypes.contains(m.mimeType))
            .findAny()
            .orElse(ALL);
    }
}
