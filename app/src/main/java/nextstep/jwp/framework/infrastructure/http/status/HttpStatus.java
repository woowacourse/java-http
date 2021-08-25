package nextstep.jwp.framework.infrastructure.http.status;

public enum HttpStatus {
    OK("200", "OK"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized"),
    NOT_FOUND("404", "Not Found"),
    INTERNAL_SEVER_ERROR("500", "Internal Server Error");

    private final String code;
    private final String nessage;

    HttpStatus(String code, String nessage) {
        this.code = code;
        this.nessage = nessage;
    }

    public String getCode() {
        return code;
    }

    public String getNessage() {
        return nessage;
    }
}
