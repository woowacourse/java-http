package nextstep.jwp.http.response;

public enum HttpResponseStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private int value;
    private String message;

    HttpResponseStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
    
    public String getLine() {
        return value + " " + message;
    }
}

