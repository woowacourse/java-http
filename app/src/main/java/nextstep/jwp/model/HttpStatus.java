package nextstep.jwp.model;

public enum HttpStatus {
    OK("200", "Oasdfasdfk"),
    FOUND("302", "Found"),
    BAD_REQUEST("400", "Bad Request"),
    NOT_FOUND("404", "Not Found");

    private final String code;
    private final String name;

    HttpStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return code + ' ' + name + ' ';
    }
}
