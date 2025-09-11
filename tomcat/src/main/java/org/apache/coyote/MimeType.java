package org.apache.coyote;

public enum MimeType {

    ANY("application/octet-stream"),
    APPLICATION_JSON("application/json"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_PLAIN("text/plain;charset=utf-8"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript");

    private final String name;

    MimeType(String name) {
        this.name = name;
    }

    public static MimeType fromResource(TextResource textResource) {
        return switch (textResource.type()) {
            case "html" -> TEXT_HTML;
            case "css" -> TEXT_CSS;
            case "js" -> TEXT_JAVASCRIPT;
            default -> ANY;
        };
    }

    public String getName() {
        return name;
    }
}
