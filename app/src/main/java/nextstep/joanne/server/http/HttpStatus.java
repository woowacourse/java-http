package nextstep.joanne.server.http;

public enum HttpStatus {
    OK("200", "OK"),

    FOUND("302", "Found"),

    BAD_REQUEST("400", "Bad Request"),

    UNAUTHORIZED("401", "Unauthorized"),

    NOT_FOUND("404", "Not Found"),

    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
    ;

    private final String value;
    private final String responsePhrase;

    HttpStatus(String value, String responsePhrase) {
        this.value = value;
        this.responsePhrase = responsePhrase;
    }

    public String value() {
        return value;
    }

    public String responsePhrase() {
        return responsePhrase;
    }
}
