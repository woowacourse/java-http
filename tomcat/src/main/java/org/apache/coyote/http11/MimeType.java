package org.apache.coyote.http11;

public enum MimeType {

    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css", "css"),
    JS("application/javascript", "js"),
    SVG("image/svg+xml", "svg"),
    ;

    private final String type;
    private final String fileExtension;

    MimeType(String type, String fileExtension) {
        this.type = type;
        this.fileExtension = fileExtension;
    }

    public String getType() {
        return type;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
