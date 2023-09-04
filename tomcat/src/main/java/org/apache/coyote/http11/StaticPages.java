package org.apache.coyote.http11;

public enum StaticPages {

    INDEX_PAGE("/index.html"),
    LOGIN_PAGE("/login.html"),
    UNAUTHORIZED_PAGE("/401.html"),
    ;

    private final String fileName;

    StaticPages(final String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
