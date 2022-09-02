package org.apache.coyote.http11;

public enum ContentType {
    HTTP("text/html"),
    CSS("text/css"),
    JS("application/javascript");

    private final String contentType;

    ContentType(final String contentType) {
        this.contentType = contentType;
    }

    public static ContentType from(final String requestUrl) {
        if (requestUrl.contains(".css")) {
            return CSS;
        }
        if (requestUrl.contains(".js")) {
            return JS;
        }
        return HTTP;
    }

    @Override
    public String toString() {
        return this.contentType;
    }
}
