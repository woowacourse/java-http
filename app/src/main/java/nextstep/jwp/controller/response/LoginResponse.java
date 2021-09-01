package nextstep.jwp.controller.response;

public class LoginResponse {

    private final String sessionId;

    public LoginResponse(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
