package nextstep.jwp;

import java.util.Arrays;

public enum HttpStatus {
    OK(200),
    FOUND(302);

    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public static String convert(int code) {
        return Arrays.stream(values())
                .filter(httpStatus -> httpStatus.code == code)
                .map(httpStatus -> code + " " + httpStatus.name())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("없는 상태코드 입니다."));
    }
}
