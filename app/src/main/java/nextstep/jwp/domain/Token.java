package nextstep.jwp.domain;

public class Token {

    private final String value;

    public Token(String value) {
        this.value = value;
    }

    public static Token fromStrategy(TokenStrategy tokenStrategy) {
        String value = tokenStrategy.token();
        return new Token(value);
    }

    public String value() {
        return this.value;
    }
}
