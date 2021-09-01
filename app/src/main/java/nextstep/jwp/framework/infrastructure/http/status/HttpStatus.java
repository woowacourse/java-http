package nextstep.jwp.framework.infrastructure.http.status;

import java.util.Arrays;

public enum HttpStatus {
    OK("200", "OK"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized"),
    NOT_FOUND("404", "Not Found"),
    INTERNAL_SEVER_ERROR("500", "Internal Server Error");

    private final String code;
    private final String message;

    HttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatus assumeFromHttpStatusPage(String url) {
        return Arrays.stream(HttpStatus.values())
            .filter(httpStatus -> httpStatus.existsWithin(url))
            .findAny()
            .orElseGet(() -> HttpStatus.OK);
    }

    private boolean existsWithin(String url) {
        String defaultHttpStatusPage = code + ".html";
        return url.endsWith(defaultHttpStatusPage);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
