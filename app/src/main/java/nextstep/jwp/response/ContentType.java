package nextstep.jwp.response;

import java.util.Arrays;
import nextstep.jwp.exception.PageNotFoundException;

public enum ContentType {
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "application/javascript"),
    HTML(".html", "text/html;charset=utf-8");

    private final String extentionType;
    private final String contentType;

    ContentType(String extensionType, String contentType) {
        this.extentionType = extensionType;
        this.contentType = contentType;
    }

    public static String contentType(String url) {
        return Arrays.stream(values())
                .filter(content -> url.endsWith(content.extentionType))
                .findAny()
                .orElseThrow(PageNotFoundException::new)
                .contentType;
    }
}
