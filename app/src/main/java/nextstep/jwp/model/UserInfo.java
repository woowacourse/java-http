package nextstep.jwp.model;

public enum UserInfo {

    ACCOUNT("account"),
    PASSWORD("password"),
    EMAIL("email");

    private final String info;

    UserInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
