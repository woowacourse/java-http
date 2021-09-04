package nextstep.jwp.handler;

public class Cookie {
    private final String name;
    private final String value;
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    public Cookie(String name, String value) {
        this(name, value, "/", false, false);
    }

    public Cookie(String name, String value, String path, boolean secure, boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
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
