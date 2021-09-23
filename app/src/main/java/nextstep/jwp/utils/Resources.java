package nextstep.jwp.utils;

public enum Resources {

    INDEX("/index.html"),
    UNAUTHORIZED("/401.html"),
    FORBIDDEN("/403.html"),
    NOT_FOUND("/404.html"),
    METHOD_NOT_ALLOWED("/405.html"),
    INTERNAL_SERVER_ERROR("/500.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html");

    private final String resource;

    Resources(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
