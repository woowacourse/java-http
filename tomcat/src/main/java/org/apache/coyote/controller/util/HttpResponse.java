package org.apache.coyote.controller.util;

public enum HttpResponse {

    KEY_VALUE_DELIMITER(": "),
    HTTP_200_OK("HTTP/1.1 200 OK "),
    HTTP_302_FOUND("HTTP/1.1 302 Found "),
    SET_COOKIE("Set-Cookie"),
    CONTENT_TYPE("Content-Type: "),
    HTML_CONTENT_TYPE("text/html;charset=utf-8;"),
    CSS_CONTENT_TYPE("text/css;charset=utf-8"),
    JS_CONTENT_TYPE("application/json"),
    CONTENT_LENGTH("Content-Length: "),
    EMPTY(""),
    ;

    public static String getContentLength(final String responseBody) {
        return CONTENT_LENGTH.value + responseBody.getBytes().length + " ";
    }

    public String getValue() {
        return this.value;
    }

    private final String value;

    HttpResponse(final String value) {
        this.value = value;
    }
}
