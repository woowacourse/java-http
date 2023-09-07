package org.apache.coyote.http.common;

public enum ContentType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "text/javascript;charset=utf-8"),
    ICO(".ico", "image/x-icon"),
    ;

    private final String fileExtension;
    private final String value;

    ContentType(final String fileExtension, final String value) {
        this.fileExtension = fileExtension;
        this.value = value;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getValue() {
        return value;
    }
}
