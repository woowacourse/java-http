package nextstep.jwp.webserver;

public abstract class BaseException extends RuntimeException {

    private final int statusCode;

    protected BaseException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
