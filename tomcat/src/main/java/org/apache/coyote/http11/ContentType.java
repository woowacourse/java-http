package org.apache.coyote.http11;

public enum ContentType {
    HTML,
    CSS
    ;

    public static ContentType from(String firstValueAccept) {
        String contentType = firstValueAccept.split("/")[1];
        try {
            return valueOf(contentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return HTML;
        }
    }

    public String getValue() {
        return this.name().toLowerCase();
    }
}
