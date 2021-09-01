package nextstep.jwp.controller.response;

public class LoginResponse {

    private final String sessionKey;
    private final String sessionValue;

    public LoginResponse(String sessionKey, String sessionValue) {
        this.sessionKey = sessionKey;
        this.sessionValue = sessionValue;
    }

    public String toCookieString() {
        return String.format("%s=%s;", sessionKey, sessionValue);
    }
}
