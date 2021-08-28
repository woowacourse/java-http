package nextstep.jwp.response;

public enum StatusCode {
    OK(200), NOT_FOUND(400);

    private final int statusNumber;

    StatusCode(int statusNumber) {
        this.statusNumber = statusNumber;
    }

    public int statusNumber() {
        return statusNumber;
    }
}
