package org.apache.coyote.http11.response;

public enum HttpContentType {
    TEXT_HTML_UTF8("text/html;charset=utf-8"),
    ;

    private final String contentTypeName;

    HttpContentType(String contentTypeName) {
        this.contentTypeName = contentTypeName;
    }

    public String contentTypeName() {
        return contentTypeName;
    }
}
