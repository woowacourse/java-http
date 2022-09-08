package nextstep.jwp.controller;

public enum Page {

    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    ;

    private final String uri;

    Page(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
