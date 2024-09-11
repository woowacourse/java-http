package org.apache.coyote.http11.request;

public enum ContentType {
    APPLICATION_JSON,
    APPLICATION_X_WWW_FORM_URLENCODED,
    ;

    public static ContentType toContentType(String contentType) {
        if (contentType.equals("application/x-www-form-urlencoded")) {
            return APPLICATION_X_WWW_FORM_URLENCODED;
        }
        if (contentType.equals("application/json")) {
            return APPLICATION_JSON;
        }
        throw new IllegalArgumentException("존재하지 않는 contentType입니다.");
    }

    public boolean isJson() {
        return this == APPLICATION_JSON;
    }

    public boolean isFromData() {
        return this == APPLICATION_X_WWW_FORM_URLENCODED;
    }
}
