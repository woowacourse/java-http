package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    APPLICATION_JSON("application/json"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    TEXT_HTML("html"),
    TEXT_JAVASCRIPT("js"),
    TEXT_CSS("css"),
    ;

    private final String httpForm;

    ContentType(String httpForm) {
        this.httpForm = httpForm;
    }

    public static ContentType toContentType(String contentType) {
        return Arrays.stream(values())
                .filter(value -> value.httpForm.equals(contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리되지 않은 contentType입니다."));
    }

    public boolean isFormUrlEncoded() {
        return this == APPLICATION_X_WWW_FORM_URLENCODED;
    }

    public String toHttpForm() {
        return httpForm;
    }
}
