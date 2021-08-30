package nextstep.jwp.http;

import java.util.Arrays;
import java.util.function.Predicate;

public enum ContentType {
    HTML("text/html;charset=utf-8", uri -> uri.endsWith(".html")),
    CSS("text/css", uri -> uri.endsWith(".css")),
    JS("application/javascript; charset=UTF-8", uri -> uri.endsWith(".js")),
    IMAGE("image/svg+xml", uri -> uri.startsWith("/assets/img")),
    NONE("text/html;charset=utf-8", uri -> true);

    private final String type;
    private final Predicate<String> expression;

    ContentType(String type, Predicate<String> expression) {
        this.type = type;
        this.expression = expression;
    }

    public static ContentType findContentType(String uri) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.expression.test(uri))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getType() {
        return type;
    }
}
