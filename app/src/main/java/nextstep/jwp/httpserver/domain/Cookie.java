package nextstep.jwp.httpserver.domain;

public class Cookie {
    private String name;
    private String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean isSessionId() {
        return name.equals("JSESSIONID");
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
