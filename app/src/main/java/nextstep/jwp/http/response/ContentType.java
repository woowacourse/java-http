package nextstep.jwp.http.response;

import java.util.Arrays;
import nextstep.jwp.exception.http.response.ContentTypeNotFoundException;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    ICO("ico", "image/x-icon");

    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(String uri) {
        String extension = uri.substring(uri.indexOf(".") + 1);

        return Arrays.stream(ContentType.values())
            .filter(contentType -> getExtension(contentType).equals(extension))
            .findFirst()
            .orElseThrow(ContentTypeNotFoundException::new);
    }

    private static String getExtension(ContentType contentType) {
        return contentType.getExtension();
    }

    public String getExtension() {
        return extension;
    }

    public String getValue() {
        return value;
    }
}
