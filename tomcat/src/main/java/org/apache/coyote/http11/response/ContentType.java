package org.apache.coyote.http11.response;

public enum ContentType {
    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css;charset=utf-8", "css"),
    JS("text/js;charset=utf-8", "js"),
    ;

    final String contentType;
    final String matchType;

    ContentType(String contentType, String matchType) {
        this.contentType = contentType;
        this.matchType = matchType;
    }

    public static String getContentType(String type) {
        for (ContentType value : values()) {
            if (value.matchType.equals(type)) {
                return value.contentType;
            }
        }
        return "";
    }

    public String getContentType() {
        return contentType;
    }
}
