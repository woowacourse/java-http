package nextstep.jwp.domain;

public class SessionID {

    private final String value;

    public SessionID(String value) {
        this.value = value;
    }

    public static SessionID fromStrategy(SessionIDStrategy sessionIDStrategy) {
        String value = sessionIDStrategy.token();
        return new SessionID(value);
    }

    public String value() {
        return this.value;
    }
}
