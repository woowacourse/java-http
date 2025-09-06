package org.apache.coyote.http11;

public enum MimeType {

    ANY("*/*"),
    APPLICATION_JSON("application/json"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_PLAIN("text/plain;charset=utf-8"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript");

    private final String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static MimeType fromResource(Resource resource) {
        if (resource.type().equals("html")) {
            return TEXT_HTML;
        } else if (resource.type().equals("css")) {
            return TEXT_CSS;
        } else if (resource.type().equals("js")) {
            return TEXT_JAVASCRIPT;
        }
        return ANY;
    }

    public String getMimeType() {
        return mimeType;
    }
}
