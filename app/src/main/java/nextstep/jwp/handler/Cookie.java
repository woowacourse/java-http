package nextstep.jwp.handler;

public class Cookie {
    private final String name;
    private final String value;
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
        this.path = "/";
        this.secure = false;
        this.httpOnly = false;
    }

    public String makeSetCookieHttpMessage() {
        String cookieMessage = String.join(
                "; ",
                name + "=" + value,
                "Path=" + path
        );
        if (secure) {
            cookieMessage += "; Secure";
        }

        if (httpOnly) {
            cookieMessage += "; HttpOnly";
        }

        return cookieMessage;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
