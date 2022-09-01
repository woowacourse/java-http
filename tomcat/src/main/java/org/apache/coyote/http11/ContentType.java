package org.apache.coyote.http11;

public enum ContentType {
    HTTP("text/html"),
    CSS("text/css"),
    JS("application/javascript");

    private final String contentType;

    ContentType(final String contentType) {
        this.contentType = contentType;
    }

    public static String from(final String requestUrl) {
        if (requestUrl.contains(".css")) {
            return CSS.getContentType();
        }
        if (requestUrl.contains(".js")) {
            return JS.getContentType();
        }
        return HTTP.getContentType();
    }

    private String getContentType() {
        return contentType;
    }
}
