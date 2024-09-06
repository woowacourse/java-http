package http;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found");

    private static final String HEADER_TEAMPLATE = "HTTP/1.1 %s %s ";
    private int code;
    private String statusName;

    HttpStatusCode(int code, String statusName) {
        this.code = code;
        this.statusName = statusName;
    }

    public String buildStatusCode() {
        return HEADER_TEAMPLATE.formatted(code, statusName);
    }
}
