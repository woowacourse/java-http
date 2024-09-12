package org.apache.coyote.http11;

public enum ContentType {
    APPLICATION_JSON,
    APPLICATION_X_WWW_FORM_URLENCODED,
    TEXT_HTML,
    TEXT_JAVASCRIPT,
    TEXT_CSS,
    ;

    public static ContentType toContentType(String contentType) {
        if (contentType.equals("application/x-www-form-urlencoded")) {
            return APPLICATION_X_WWW_FORM_URLENCODED;
        }
        if (contentType.equals("application/json")) {
            return APPLICATION_JSON;
        }
        if (contentType.equals("html")) {
            return TEXT_HTML;
        }
        if (contentType.equals("css")) {
            return TEXT_CSS;
        }
        if (contentType.equals("js")) {
            return TEXT_JAVASCRIPT;
        }
        throw new IllegalArgumentException("처리되지 않은 contentType입니다.");
    }

    public boolean isFormUrlEncoded() {
        return this == APPLICATION_X_WWW_FORM_URLENCODED;
    }

    public String toHttpForm() {
        return name().replaceFirst("_", "/").toLowerCase();
    }
}
