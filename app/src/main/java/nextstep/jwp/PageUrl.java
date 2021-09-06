package nextstep.jwp;

public enum PageUrl {
    INDEX_PAGE("/index.html"),
    LOGIN_PAGE("/login.html"),
    REGISTER_PAGE("/register.html"),
    UNAUTHORIZED_PAGE("/401.html");

    private final String path;

    PageUrl(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
