package nextstep.jwp.model;

public enum PathType {

    BASE("/"),
    INDEX("/index"),
    LOGIN("/login"),
    REGISTER("/register"),
    UNAUTHORIZED("/401"),
    NOT_FOUND("/404");

    private String value;

    PathType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public String resource() {
        return value + FileType.HTML.extension();
    }
}
