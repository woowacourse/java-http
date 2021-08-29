package nextstep.jwp.model;

public enum PathType {

    BASE_PATH("/"),
    INDEX_PATH("/index"),
    LOGIN_PATH("/login"),
    REGISTER_PATH("/register"),
    UNAUTHORIZED("/401");

    private String value;

    PathType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
