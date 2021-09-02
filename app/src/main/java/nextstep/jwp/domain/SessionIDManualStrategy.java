package nextstep.jwp.domain;

public class SessionIDManualStrategy implements SessionIDStrategy {

    private final String value;

    public SessionIDManualStrategy(String value) {
        this.value = value;
    }

    @Override
    public String token() {
        return this.value;
    }
}
