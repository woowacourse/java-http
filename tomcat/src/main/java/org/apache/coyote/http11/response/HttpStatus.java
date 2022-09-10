package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK", ""),
    FOUND(302, "FOUND", ""),
    UNAUTHORIZED(401, "UNAUTHORIZED", "/401.html"),
    NOT_FOUND(404, "NOT FOUND", "/404.html");

    private static final String END_OF_STATUS = " ";

    private final int code;
    private final String message;
    private final String filePath;

    HttpStatus(int code, String message, String filePath) {
        this.code = code;
        this.message = message;
        this.filePath = filePath;
    }

    public String statusToResponse() {
        return code + END_OF_STATUS + message + " ";
    }

    public String getFilePath() {
        return filePath;
    }
}
