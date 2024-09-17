package jakarta.http;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    JS("text/javascript", ".js"),
    CSS("text/css", ".css"),
    SVG("image/svg+xml", ".svg"),
    PLAIN("text/plain", ""),
    APPLICATION_X_WWW_FORM_URL_ENCODED("application/x-www-form-urlencoded", ""),
    ;

    private static final String CHARSET_UTF_8 = ";charset=utf-8";

    private final String name;
    private final String extension;

    ContentType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public static ContentType from(String contentTypeName) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.name.equals(contentTypeName))
                .findFirst()
                .orElse(PLAIN);
    }

    public static ContentType determineContentType(String resourcePath) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> isPathEndWithType(resourcePath, contentType))
                .findFirst()
                .orElse(ContentType.PLAIN);
    }

    private static boolean isPathEndWithType(String resourcePath, ContentType contentType) {
        return resourcePath.endsWith(contentType.getExtension());
    }

    public String getName() {
        return name + CHARSET_UTF_8;
    }

    public String getExtension() {
        return extension;
    }
}
