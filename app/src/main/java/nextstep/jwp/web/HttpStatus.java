package nextstep.jwp.web;

public enum HttpStatus {
    OK(200), CREATED(201);

    private final int value;

    HttpStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
