package nextstep.jwp.http.http_response;

import nextstep.jwp.exception.NotFoundContentTypeException;

import java.util.Arrays;
import java.util.function.Predicate;

public enum JwpContentType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    ICON(".ico", "image/x-icon;charset=utf-8"),
    SVG(".svg", "image/svg+xml;charset=utf-8")
    ;

    private final Predicate<String> condition;
    private final String contentType;

    JwpContentType(String type, String contentType) {
        this(uri -> uri.endsWith(type), contentType);
    }

    JwpContentType(Predicate<String> condition, String contentType) {
        this.condition = condition;
        this.contentType = contentType;
    }

    public static String find(String resourceUri) {
        return Arrays.stream(values())
                .filter(type -> type.isSatisfied(resourceUri))
                .findAny()
                .orElseThrow(NotFoundContentTypeException::new)
                .contentType;
    }

    private boolean isSatisfied(String resourceFile) {
        return this.condition.test(resourceFile);
    }

    public String getContentType() {
        return contentType;
    }
}
