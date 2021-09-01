package nextstep.jwp.domain;

public class ManualStrategy implements TokenStrategy {

    private final String value;

    public ManualStrategy(String value) {
        this.value = value;
    }

    @Override
    public String token() {
        return this.value;
    }
}
