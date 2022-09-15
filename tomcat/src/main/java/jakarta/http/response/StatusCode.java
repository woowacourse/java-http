package jakarta.http.response;

public enum StatusCode {

    OK("200", "OK"),
    FOUND("302", "FOUND"),
    CREATE("201", "CREATE");

    private final String code;
    private final String message;

    StatusCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
