package nextstep.jwp.model;

public enum FileType {

    HTML("text/html; charset=utf-8", ".html"),
    CSS("text/css; charset=utf-8", ".css"),
    JS("application/javascript; charset=utf-8", ".js"),
    ICO("image/x-icon", ".ico"),
    SVG("image/svg+xml", ".svg");

    private String contentType;
    private String extension;

    FileType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String contentType() {
        return contentType;
    }

    public String extension() {
        return extension;
    }
}
