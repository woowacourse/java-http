package nextstep.jwp.handler;

public class DashboardException extends RuntimeException {

    private final int statusCode;

    public DashboardException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
