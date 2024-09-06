package org.apache.coyote.http11;

public enum ContentType {

    HTML("text/html", ".html"),
    JS("text/javascript", ".js"),
    CSS("text/css", ".css"),
    SVG("image/svg+xml", ".svg"),
    PLAIN("text/plain", ""),
    ;

    private final String name;
    private final String extension;

    ContentType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }
}
