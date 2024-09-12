package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    APPLICATION_JSON("application/json", "application/json"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", "application/x-www-form-urlencoded"),
    TEXT_HTML("html", "text/html"),
    TEXT_JAVASCRIPT("js", "text/javascript"),
    TEXT_CSS("css", "text/css"),
    ;

    private final String httpInputForm;
    private final String httpOutputForm;

    ContentType(String httpInputForm, String httpOutputForm) {
        this.httpInputForm = httpInputForm;
        this.httpOutputForm = httpOutputForm;
    }

    public static ContentType toContentType(String contentType) {
        return Arrays.stream(values())
                .filter(value -> value.httpInputForm.equals(contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리되지 않은 contentType입니다."));
    }

    public boolean isFormUrlEncoded() {
        return this == APPLICATION_X_WWW_FORM_URLENCODED;
    }

    public String toHttpOutputForm() {
        return httpOutputForm;
    }
}
