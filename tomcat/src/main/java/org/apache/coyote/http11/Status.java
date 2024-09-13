package org.apache.coyote.http11;

public enum Status {

    OK("OK",200),
    FOUND("FOUND",302),
    ;

    private String name;
    private int code;

    Status(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
