package org.apache.coyote.http11.resource;

public enum ContentType {

    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    TEXT_JAVASCRIPT("text/javascript", "js");

    private final String value;

    private final String symbol;

    ContentType(final String value, final String symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getValue() {
        return value;
    }
}
