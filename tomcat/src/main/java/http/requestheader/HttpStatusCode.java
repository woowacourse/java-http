package http.requestheader;

public enum HttpStatusCode {
    OK(200);

    private static final String HEADER_TEAMPLATE = "HTTP/1.1 %s %s ";
    int code;

    HttpStatusCode(int code) {
        this.code = code;
    }

    public String buildStatusCode() {
        return HEADER_TEAMPLATE.formatted(code, this.name());
    }
}
