package nextstep.jwp.ui.response;

import java.util.Arrays;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    UNAUTHORIZED(401);

    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public static String convert(int code) {
        return Arrays.stream(values())
                .filter(httpStatus -> httpStatus.code == code)
                .map(httpStatus -> code + " " + httpStatus.name())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 상태코드 입니다."));
    }
}
