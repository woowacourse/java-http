package nextstep.jwp.model;

public enum FileType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("application/js", ".js"),
    ICO("image/x-icon", ".ico");

    private String type;
    private String extension;

    FileType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public String type() {
        return type;
    }

    public String extension() {
        return extension;
    }
}
