package nextstep.jwp.model;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js");

    String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }
}
