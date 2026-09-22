package org.apache.coyote;

public enum HttpStatus {

    OK(200, "OK", null),
    FOUND(302, "Found", null),
    NOT_FOUND(404, "Not Found", "/404.html"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", null),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "/500.html");

    private final int code;
    private final String reasonPhrase;
    private final String errorPage;

    HttpStatus(int code, String reasonPhrase, String errorPage) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
        this.errorPage = errorPage;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getStatusLine() {
        return code + " " + reasonPhrase;
    }

    public String getErrorPage() {
        return errorPage;
    }
}
