package common;

import static java.lang.Integer.MIN_VALUE;

public enum ResponseStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    EMPTY_RESPONSE_STATUS(MIN_VALUE, "Empty Response Status"),
    ;

    private final int code;
    private final String response;

    ResponseStatus(int code, String response) {
        this.code = code;
        this.response = response;
    }

    public String codeMessage() {
        return String.valueOf(code);
    }

    public String responseMessage() {
        return response;
    }
}
