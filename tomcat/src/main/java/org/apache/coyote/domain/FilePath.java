package org.apache.coyote.domain;

public class FilePath {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String EXTENSION_REGEX = ".";
    private static final String HOME_URI = "/";
    private static final String HTML_EXTENSION = ".html";

    private final String value;

    private FilePath(String value) {
        this.value = value;
    }

    public static FilePath from(String uri) {
        if (uri.contains(QUERY_STRING_DELIMITER)) {
            uri = uri.split("\\?")[0];
        }
        if (!uri.contains(EXTENSION_REGEX) && !uri.equals(HOME_URI)) {
            uri += HTML_EXTENSION;
        }
        return new FilePath(uri);
    }

    public String getValue() {
        return value;
    }
}
