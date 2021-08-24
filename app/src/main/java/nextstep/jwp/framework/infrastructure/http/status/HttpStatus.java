package nextstep.jwp.framework.infrastructure.http.status;

public enum HttpStatus {
    UNAUTHORIZED("401", "Unauthorized"),
    NOT_FOUND("404", "Not Found"),
    INTERNAL_SEVER_ERROR("500", "Internal Server Error");

    private final String errorCode;
    private final String errorMessage;

    HttpStatus(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
