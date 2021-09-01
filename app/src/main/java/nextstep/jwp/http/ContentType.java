package nextstep.jwp.http;

import nextstep.jwp.exception.NoSuchContentTypeException;

import java.util.Arrays;
import java.util.function.Predicate;

public enum ContentType {
    HTML("text/html", path -> path.endsWith(".html")),
    CSS("text/css", path -> path.endsWith(".css")),
    JS("text/js", path -> path.endsWith(".js")),
    ICO("image/x-icon", path -> path.endsWith(".ico")),
    SVG("image/x-icon", path -> path.endsWith(".svg")),
    NONE("text/html", path -> true);

    private final String mimeType;
    private final Predicate<String> expression;

    ContentType(final String mimeType, final Predicate<String> expression) {
        this.mimeType = mimeType;
        this.expression = expression;
    }

    public static ContentType findByUrl(final String path) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.expression.test(path))
                .findFirst()
                .orElseThrow(NoSuchContentTypeException::new);
    }

    public boolean hasFileExtension() {
        return !this.equals(NONE);
    }

    public String getMimeType() {
        return mimeType;
    }
}
