package nextstep.jwp.handler;

public class Cookie {
    private String name;
    private String value;
    private String path;
    private boolean secure;
    private boolean httpOnly;

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
}
