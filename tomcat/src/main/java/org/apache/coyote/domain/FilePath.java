package org.apache.coyote.domain;

public class FilePath {
    private String value;

    private FilePath(String value) {
        this.value = value;
    }

    public static FilePath from(String uri) {
        if (uri.contains("?")) {
            uri = uri.split("\\?")[0];
        }
        if (!uri.contains(".") && !uri.equals("/")) {
            uri += ".html";
        }
        return new FilePath(uri);
    }

    public String getValue() {
        return value;
    }
}
