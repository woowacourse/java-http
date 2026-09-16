package org.apache.coyote.http11;

public enum ContentType {
    JS("application/javascript;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    HTML("text/html;charset=utf-8"),
    PLAIN("text/plain;charset=utf-8");

    private String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public static String from(String path) {
        if (path.endsWith(".html")) {
            return HTML.getContentType();
        }
        if (path.endsWith(".css")) {
            return CSS.getContentType();
        }
        if (path.endsWith(".js")) {
            return JS.getContentType();
        }
        return PLAIN.getContentType();
    }

    public String getContentType() {
        return contentType;
    }
}
