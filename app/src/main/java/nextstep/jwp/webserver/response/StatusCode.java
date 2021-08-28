package nextstep.jwp.webserver.response;

import java.util.Arrays;

public enum StatusCode {
    OK(200),
    FOUND(302),
    NOT_FOUND(404),
    SERVER_ERROR(500),
    ;

    private final int statusNumber;

    StatusCode(int statusNumber) {
        this.statusNumber = statusNumber;
    }

    public static StatusCode findByNumber(int statusNumber) {
        return Arrays.stream(values()).filter(code -> code.statusNumber == statusNumber)
                .findAny()
                .orElseThrow();
    }

    public int statusNumber() {
        return statusNumber;
    }
}
