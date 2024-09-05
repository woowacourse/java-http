package org.apache.coyote.response;

public enum StatusCode implements Assemblable {

    OK(200),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    ;

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    @Override
    public void assemble(StringBuilder builder) {
        String status = name().replace("_", " ").toLowerCase();
        builder.append("%s %d\r\n".formatted(status, code));
    }
}
