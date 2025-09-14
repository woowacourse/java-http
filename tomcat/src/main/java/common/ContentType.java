package common;

import static common.HttpConstants.KEY_VALUE_SEPARATOR;
import static common.HttpConstants.VALUE_SEPARATOR;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_JSON("application/json"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded")
    ;

    public static final String HEADER_NAME = "Content-Type";
    public static final String CHARSET_NAME = "charset";
    public static final String HTML_EXTENSION = ".html";
    public static final String CSS_EXTENSION = ".css";
    public static final String JS_EXTENSION = ".js";
    public static final String CONTENT_TYPE_SEPARATOR = ";";

    private static final ContentType DEFAULT_REQUEST_CONTENT_TYPE = ContentType.TEXT_PLAIN;
    public static final ContentType DEFAULT_RESPONSE_CONTENT_TYPE = ContentType.TEXT_HTML;

    private final String mimeType;
    private final Charset defaultCharset = StandardCharsets.UTF_8;

    // For Request
    public static ContentType fromHeader(final String contentTypeHeader) {
        if (contentTypeHeader == null || contentTypeHeader.isEmpty()) {
            return DEFAULT_REQUEST_CONTENT_TYPE;
        }

        final String mimeType = contentTypeHeader.split(CONTENT_TYPE_SEPARATOR)[0].trim().toLowerCase();

        for (final ContentType type : ContentType.values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }

        return DEFAULT_REQUEST_CONTENT_TYPE;
    }

    // For Response
    public static ContentType fromFilePath(final String path) {
        final String lowerPath = path.toLowerCase();

        if (lowerPath.endsWith(CSS_EXTENSION)) {
            return TEXT_CSS;
        }

        if (lowerPath.endsWith(JS_EXTENSION)) {
            return APPLICATION_JAVASCRIPT;
        }

        return DEFAULT_RESPONSE_CONTENT_TYPE;
    }

    public String getMimeTypeAndCharset() {
        return "%s%s%s%s%s".formatted(
                mimeType,
                VALUE_SEPARATOR,
                CHARSET_NAME, KEY_VALUE_SEPARATOR, defaultCharset.name());
    }
}
