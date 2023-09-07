package org.apache.coyote.controller.util;

public enum FileResolver {

    INDEX_HTML("index.html"),
    CSS("css/styles.css"),
    SCRIPTS_JS("js/scripts.js"),
    LOGIN("login.html"),
    REGISTER("register.html"),
    HTML_401("401.html"),
    ;

    private final String fileName;

    FileResolver(final String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
