package nextstep.jwp.web.http;

import java.util.Arrays;
import java.util.List;

public enum MimeType {
    TEXT_ALL("text/*"),
    TEXT_PLAIN("text/plain"),
    TEXT_CSS("text/css"),
    TEXT_JS("text/js"),
    TEST_HTML("text/html"),
    ALL("*/*");

    private final String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static MimeType findByName(List<String> mimeType) {
        return Arrays.stream(values())
            .filter(m -> mimeType.contains(m.mimeType))
            .findAny()
            .orElse(ALL);
    }
}
