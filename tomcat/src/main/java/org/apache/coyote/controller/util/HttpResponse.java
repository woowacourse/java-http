package org.apache.coyote.controller.util;

import org.apache.coyote.http11.http.HttpSession;

public enum HttpResponse {

    KEY_VALUE_DELIMITER(": "),
    HTTP_200_OK("HTTP/1.1 200 OK "),
    HTTP_302_FOUND("HTTP/1.1 302 Found "),
    SET_COOKIE("Set-Cookie: "),
    LOCATION("Location: "),
    JSESSION("JSESSIONID="),
    CONTENT_TYPE("Content-Type: "),
    HTML_CONTENT_TYPE("text/html;charset=utf-8 "),
    CSS_CONTENT_TYPE("text/css;charset=utf-8"),
    JS_CONTENT_TYPE("application/json"),
    CONTENT_LENGTH("Content-Length: "),
    EMPTY(""),
    BLANK(" "),
    ;

    private final String value;

    HttpResponse(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static String createSetSession(final HttpSession session) {
        if (session == null) {
            return EMPTY.value;
        }
        return SET_COOKIE.value + JSESSION.value + session.getId();
    }

    public static String getContentLength(final String responseBody) {
        return CONTENT_LENGTH.value + responseBody.getBytes().length + " ";
    }

    public static String getLocation(final FileResolver file) {
        return LOCATION.value + file.getFileName() + BLANK.getValue();
    }

    public String getContentType() {
        return CONTENT_TYPE.value + this.value;
    }
}
