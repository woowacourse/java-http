package nextstep.jwp.web;

public enum HttpStatus {
    OK(200), CREATED(201), NOT_FOUND(404), METHOD_NOT_ALLOWED(405);
    private final int value;

    HttpStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
